package com.fmsp.srvapidataregister.modules.users.service;

import com.fmsp.srvapidataregister.modules.users.dto.RegistroDTO;
import com.fmsp.srvapidataregister.modules.users.dto.UsuarioDTO;

import java.util.Optional;

public interface IUsuarioService {
    Optional<UsuarioDTO> getUsuarioByUsername(String username);
    UsuarioDTO saveUsuario(UsuarioDTO usuario);
    UsuarioDTO registerUsuario(RegistroDTO registroDTO, String uuid);
}
