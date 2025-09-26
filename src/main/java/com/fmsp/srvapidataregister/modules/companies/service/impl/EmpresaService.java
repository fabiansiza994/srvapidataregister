package com.fmsp.srvapidataregister.modules.companies.service.impl;

import com.fmsp.srvapidataregister.core.exceptions.InternalServerException;
import com.fmsp.srvapidataregister.modules.companies.dto.EmpresaDTO;
import com.fmsp.srvapidataregister.modules.companies.entity.Empresa;
import com.fmsp.srvapidataregister.modules.companies.repository.EmpresaRepository;
import com.fmsp.srvapidataregister.modules.companies.service.IEmpresaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService implements IEmpresaService {

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
}
