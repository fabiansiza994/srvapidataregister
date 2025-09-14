package com.fmsp.srvapidataregister.modules.pais.service.impl;

import com.fmsp.srvapidataregister.modules.pais.dto.PaisDTO;
import com.fmsp.srvapidataregister.modules.pais.repository.PaisRepository;
import com.fmsp.srvapidataregister.modules.pais.service.IPaisService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaisService implements IPaisService {

    private final PaisRepository paisRepository;
    private final ModelMapper modelMapper;

    public PaisService(PaisRepository paisRepository, ModelMapper modelMapper) {
        this.paisRepository = paisRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PaisDTO findById(Long id) {
        var pais = paisRepository.findById(id).orElse(null);;
        return modelMapper.map(pais, PaisDTO.class);
    }

    @Override
    public List<PaisDTO> findAllActive() {
        return paisRepository.findAll().stream().map(pais -> modelMapper.map(pais, PaisDTO.class)).toList();
    }
}
