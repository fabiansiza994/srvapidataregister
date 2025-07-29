package com.fmsp.srvapidataregister.modules.groups.service.impl;

import com.fmsp.srvapidataregister.modules.groups.dto.GrupoDTO;
import com.fmsp.srvapidataregister.modules.groups.entity.Grupo;
import com.fmsp.srvapidataregister.modules.groups.repository.GrupoRepository;
import com.fmsp.srvapidataregister.modules.groups.service.IGrupoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class GrupoService implements IGrupoService {

    private final GrupoRepository grupoRepository;
    private final ModelMapper modelMapper;

    public GrupoService(GrupoRepository grupoRepository, ModelMapper modelMapper) {
        this.grupoRepository = grupoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public GrupoDTO save(GrupoDTO grupoDTO) {
        var grupo = modelMapper.map(grupoDTO, Grupo.class);
        return modelMapper.map(grupoRepository.save(grupo), GrupoDTO.class);
    }
}
