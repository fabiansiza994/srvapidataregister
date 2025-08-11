package com.fmsp.srvapidataregister.modules.pais.service.impl;

import com.fmsp.srvapidataregister.modules.pais.dto.PaisDTO;
import com.fmsp.srvapidataregister.modules.pais.repository.IPaisRepository;
import com.fmsp.srvapidataregister.modules.pais.service.IPaisService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PaisService implements IPaisService {

    private final IPaisRepository paisRepository;
    private final ModelMapper modelMapper;

    public PaisService(IPaisRepository paisRepository, ModelMapper modelMapper) {
        this.paisRepository = paisRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PaisDTO findById(Long id) {
        var pais = paisRepository.findById(id).orElse(null);;
        return modelMapper.map(pais, PaisDTO.class);
    }
}
