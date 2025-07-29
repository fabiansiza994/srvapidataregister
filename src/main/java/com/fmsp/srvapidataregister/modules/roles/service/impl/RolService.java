package com.fmsp.srvapidataregister.modules.roles.service.impl;

import com.fmsp.srvapidataregister.modules.roles.dto.RolDTO;
import com.fmsp.srvapidataregister.modules.roles.entity.Rol;
import com.fmsp.srvapidataregister.modules.roles.repository.RolRepository;
import com.fmsp.srvapidataregister.modules.roles.service.IRolService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class RolService implements IRolService {

    private final RolRepository rolRepository;
    private final ModelMapper modelMapper;

    public RolService(RolRepository rolRepository, ModelMapper modelMapper) {
        this.rolRepository = rolRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RolDTO save(RolDTO rolDTO) {
        var rol = rolRepository.save(
                modelMapper.map(rolDTO, Rol.class)
        );
        return modelMapper.map(rol, RolDTO.class);
    }

    @Override
    public RolDTO findById(Long id) {
        var rol = rolRepository.findById(id).orElse(null);
        return modelMapper.map(rol, RolDTO.class);
    }
}
