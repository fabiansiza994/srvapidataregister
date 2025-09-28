package com.fmsp.srvapidataregister.modules.methodPayment.service.impl;

import com.fmsp.srvapidataregister.core.exceptions.CustomServiceException;
import com.fmsp.srvapidataregister.modules.auth.service.PermisoService;
import com.fmsp.srvapidataregister.modules.jobs.repository.TrabajoRepository;
import com.fmsp.srvapidataregister.modules.methodPayment.dto.FormaPagoCreateDTO;
import com.fmsp.srvapidataregister.modules.methodPayment.dto.FormaPagoDTO;
import com.fmsp.srvapidataregister.modules.methodPayment.entity.FormaPago;
import com.fmsp.srvapidataregister.modules.methodPayment.repository.MOPRepository;
import com.fmsp.srvapidataregister.modules.methodPayment.service.IMOPService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MOPService implements IMOPService {

    private final PermisoService permisoService;
    private final TrabajoRepository trabajoRepository;
    private final MOPRepository repository;
    private final ModelMapper modelMapper;

    public MOPService(PermisoService permisoService, TrabajoRepository trabajoRepository, MOPRepository repository, ModelMapper modelMapper) {
        this.permisoService = permisoService;
        this.trabajoRepository = trabajoRepository;
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<FormaPagoDTO> findById(Long id) {
        var formaPago = repository.findById(id);
        return formaPago.map(f -> modelMapper.map(f, FormaPagoDTO.class));
    }

    @Override
    public FormaPagoCreateDTO save(FormaPagoCreateDTO formapagoDTO) {
        String uuid = UUID.randomUUID().toString();
        if (!permisoService.hasRole("ADMIN")) {
            throw new CustomServiceException(uuid, "E003", "No autorizado para eliminar formas de pago");
        }
        var formaPago = repository.save(modelMapper.map(formapagoDTO, FormaPago.class));
        return modelMapper.map(formaPago, FormaPagoCreateDTO.class);
    }

    @Override
    public List<FormaPagoDTO> findAll() {
        return repository.findAll().stream().map(mop -> modelMapper.map(mop, FormaPagoDTO.class)).toList();
    }

    @Override
    public List<FormaPagoDTO> findAllByEmpresa(Long id) {
        return repository.findAllByEmpresa_Id(id)
                .stream()
                .map(mop -> modelMapper.map(mop, FormaPagoDTO.class))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        String uuid = UUID.randomUUID().toString();
        if (!permisoService.hasRole("ADMIN")) {
            throw new CustomServiceException(uuid, "E003", "No autorizado para eliminar formas de pago");
        }
        var idDb = repository.findById(id);
        if(idDb.isEmpty()){
            throw new CustomServiceException("delete", "E003", "Forma de pago no encontrada");
        }
        var formaPago = trabajoRepository.countByFormaPago_Id(id);
        if(formaPago > 0){
            throw new CustomServiceException("delete", "E003", "No es posible eliminar, tiene {"+formaPago+"} trabajos asociados");
        }
        repository.deleteById(id);
    }

    @Override
    public FormaPagoDTO update(FormaPagoDTO formapagoDTO) {
        String uuid = UUID.randomUUID().toString();
        if (!permisoService.hasRole("ADMIN")) {
            throw new CustomServiceException(uuid, "E003", "No autorizado para eliminar formas de pago");
        }
        var fpUpdated = repository.update(formapagoDTO.getFormaPago(), formapagoDTO.getId());
        if(fpUpdated == 0){
            throw new CustomServiceException("update", "E001", "Error en la actualizacion");
        }
        return modelMapper.map(fpUpdated, FormaPagoDTO.class);
    }
}
