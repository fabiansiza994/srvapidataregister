package com.fmsp.srvapidataregister.modules.sector.service.impl;

import com.fmsp.srvapidataregister.modules.sector.dto.SectorDTO;
import com.fmsp.srvapidataregister.modules.sector.repository.ISectorRepository;
import com.fmsp.srvapidataregister.modules.sector.service.ISectorService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectorService implements ISectorService {

    private final ISectorRepository sectorRepository;
    private final ModelMapper modelMapper;

    public SectorService(ISectorRepository sectorRepository, ModelMapper modelMapper) {
        this.sectorRepository = sectorRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public SectorDTO findSectorById(Long id) {
        var sector = sectorRepository.findById(id).orElse(null);
        ;
        return modelMapper.map(sector, SectorDTO.class);
    }

    @Override
    public List<SectorDTO> findAllActive() {
        return sectorRepository.findAll().stream().map(sector -> modelMapper.map(sector, SectorDTO.class)).toList();
    }
}
