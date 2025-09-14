package com.fmsp.srvapidataregister.modules.methodPayment.service.impl;

import com.fmsp.srvapidataregister.modules.methodPayment.dto.FormaPagoDTO;
import com.fmsp.srvapidataregister.modules.methodPayment.repository.MOPRepository;
import com.fmsp.srvapidataregister.modules.methodPayment.service.IMOPService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
