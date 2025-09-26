package com.fmsp.srvapidataregister.modules.methodPayment.service.impl;

import com.fmsp.srvapidataregister.core.exceptions.CustomServiceException;
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

    private final MOPRepository repository;
    private final ModelMapper modelMapper;

    public MOPService(MOPRepository repository, ModelMapper modelMapper) {
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
        var idDb = repository.findById(id);
        if(idDb.isEmpty()){
            throw new CustomServiceException("delete", "E003", "Forma de pago no encontrada");
        }
        repository.deleteById(id);
    }

    @Override
    public FormaPagoDTO update(FormaPagoDTO formapagoDTO) {
        var fpUpdated = repository.update(formapagoDTO.getFormaPago(), formapagoDTO.getId());
        if(fpUpdated == 0){
            throw new CustomServiceException("update", "E001", "Error en la actualizacion");
        }
        return modelMapper.map(fpUpdated, FormaPagoDTO.class);
    }
}
