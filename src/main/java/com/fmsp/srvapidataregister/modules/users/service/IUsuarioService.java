package com.fmsp.srvapidataregister.modules.users.service;

import com.fmsp.srvapidataregister.modules.users.dto.*;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IUsuarioService {
    Optional<UsuarioDTO> getUsuarioById(Long id);

    Optional<UsuarioDTO> getUsuarioByUsername(String username);

    UsuarioDTO saveUsuario(UsuarioDTO usuario);

    UsuarioDTO registerUsuario(RegistroDTO registroDTO, String uuid);

    Page<UsuarioListDTO> list(int page, int size, String sortBy, String direction);

    Page<UsuarioListDTO> search(String q, int page, int size, String sortBy, String direction);

    UsuarioDetailDTO detail(Long id, String uuid);

    UsuarioDetailDTO update(Long id, UsuarioUpdateDTO dto, String uuid);

    void delete(Long id, String uuid);

    UserProfileDTO profile(Long id, String uuid);

    int updateBlockValue(int value, Boolean isBlocked, Long id);

}
