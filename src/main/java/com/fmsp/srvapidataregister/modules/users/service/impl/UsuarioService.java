package com.fmsp.srvapidataregister.modules.users.service.impl;

import com.fmsp.srvapidataregister.modules.users.dto.UsuarioDTO;
import com.fmsp.srvapidataregister.modules.users.entity.Usuario;
import com.fmsp.srvapidataregister.modules.users.repository.UsuarioRepository;
import com.fmsp.srvapidataregister.modules.users.service.IUsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<UsuarioDTO> getUsuarioByUsername(String username) {
        Optional<Usuario> user = usuarioRepository.findByUsername(username);
        return Optional.ofNullable(modelMapper.map(user.get(), UsuarioDTO.class));
    }

    @Override
    public UsuarioDTO saveUsuario(UsuarioDTO user) {
        var userDb = usuarioRepository.save(modelMapper.map(user, Usuario.class));
        return modelMapper.map(userDb, UsuarioDTO.class);
    }
}
