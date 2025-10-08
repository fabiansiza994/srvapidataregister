package com.fmsp.srvapidataregister.modules.companies.service.impl;

import com.fmsp.srvapidataregister.core.exceptions.InternalServerException;
import com.fmsp.srvapidataregister.modules.companies.dto.CompanySettingsDTO;
import com.fmsp.srvapidataregister.modules.companies.dto.EmpresaDTO;
import com.fmsp.srvapidataregister.modules.companies.entity.Empresa;
import com.fmsp.srvapidataregister.modules.companies.repository.EmpresaRepository;
import com.fmsp.srvapidataregister.modules.companies.service.ICompanyService;
import com.fmsp.srvapidataregister.modules.companies.service.IEmpresaService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaService implements IEmpresaService, ICompanyService {

    private final EmpresaRepository empresaRepository;
    private final ModelMapper modelMapper;

    public EmpresaService(EmpresaRepository empresaRepository, ModelMapper modelMapper) {
        this.empresaRepository = empresaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public EmpresaDTO save(EmpresaDTO empresaDTO) {
        var empresaDB = empresaRepository.findByNit(empresaDTO.getNit());
        if (empresaDB != null) {
            throw new InternalServerException("", "E002", "ya existe una empresa asociada a ese NIT");
        }
        var empresa = modelMapper.map(empresaDTO, Empresa.class);
        empresa.setUserLimit(3);
        empresaRepository.save(empresa);
        return modelMapper.map(empresa, EmpresaDTO.class);
    }

    @Override
    public EmpresaDTO findById(Long id) {
        var empresa = empresaRepository.findById(id);
        return modelMapper.map(empresa, EmpresaDTO.class);
    }

    @Override
    public EmpresaDTO findByNombre(String nombre) {
        var empresa = empresaRepository.findByNombre(nombre);
        if(empresa == null) {
            return null;
        }
        return modelMapper.map(empresa, EmpresaDTO.class);
    }

    public Integer getLimit(String nombre) {
        var empresa = empresaRepository.findByNombre(nombre);
        if(empresa == null) {
            return null;
        }
        return empresa.getUserLimit();
    }

    private Empresa getOrThrow(Long id){
        return empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada: " + id));
    }

    private CompanySettingsDTO map(Empresa e){
        return new CompanySettingsDTO(e.getId(), e.getNombre(), e.getAllowView(), e.getAllowEdit());
    }

    @Override
    @Transactional(readOnly = true)
    public CompanySettingsDTO getSettings(Long empresaId, String idTx) {
        return map(getOrThrow(empresaId));
    }

    @Override
    @Transactional
    public CompanySettingsDTO setAllowView(Long empresaId, boolean enabled, String idTx) {
        Empresa e = getOrThrow(empresaId);
        e.setAllowView(enabled);
        // coherencia: si se apaga ver, también apaga editar
        if (!enabled) e.setAllowEdit(false);
        empresaRepository.save(e);
        return map(e);
    }

    @Override
    @Transactional
    public CompanySettingsDTO setAllowEdit(Long empresaId, boolean enabled, String idTx) {
        Empresa e = getOrThrow(empresaId);
        // coherencia: si se enciende editar, ver debe estar encendido
        if (enabled && Boolean.FALSE.equals(e.getAllowView())) {
            e.setAllowView(true);
        }
        e.setAllowEdit(enabled);
        empresaRepository.save(e);
        return map(e);
    }

    @Override
    @Transactional
    public CompanySettingsDTO updateSettings(Long empresaId, boolean allowView, boolean allowEdit, String idTx) {
        Empresa e = getOrThrow(empresaId);
        if (allowEdit) allowView = true; // regla de negocio
        e.setAllowView(allowView);
        e.setAllowEdit(allowEdit);
        empresaRepository.save(e);
        return map(e);
    }
}
