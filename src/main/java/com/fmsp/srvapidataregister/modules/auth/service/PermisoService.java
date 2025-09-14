package com.fmsp.srvapidataregister.modules.auth.service;

import com.fmsp.srvapidataregister.modules.clients.repository.ClienteRepository;
import com.fmsp.srvapidataregister.modules.users.dto.UsuarioDTO;
import com.fmsp.srvapidataregister.modules.users.entity.Usuario;
import com.fmsp.srvapidataregister.modules.users.service.IUsuarioService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("permisoService") // asegura el nombre del bean usado en SpEL: @permisoService
@Transactional(readOnly = true)
public class PermisoService {

    private final IUsuarioService usuarioService;
    private final ClienteRepository clienteRepository;

    public PermisoService(IUsuarioService usuarioService, ClienteRepository clienteRepository) {
        this.usuarioService = usuarioService;
        this.clienteRepository = clienteRepository;
    }

    /**
     * Permite que un USER acceda si el cliente pertenece al mismo grupo que el usuario autenticado.
     */
    public boolean mismoGrupo(Long clienteId) {
        Optional<UsuarioDTO> actual = getUsuarioActual();
        if (actual.isEmpty()) return false;

        Long grupoUsuario = Optional.ofNullable(actual.get().getGrupo())
                .map(g -> g.getId())
                .orElse(null);
        if (grupoUsuario == null) return false;

        // Consulta liviana: solo el id del grupo del cliente
        Optional<Long> grupoCliente = clienteRepository.findGrupoIdByClienteId(clienteId);
        return grupoCliente.isPresent() && grupoCliente.get().equals(grupoUsuario);
    }

    private Optional<UsuarioDTO> getUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();

        // Aquí asumo que tu usuario se identifica por username en el Authentication
        String username = auth.getName();
        return usuarioService.getUsuarioByUsername(username);
    }

    public boolean hasRole(String role) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> {
                    var name = a.getAuthority();
                    return name.equals("ROLE_" + role) || name.equals(role);
                });
    }

    public Long grupoIdActualOrNull() {
        return usuarioActual()
                .map(u -> u.getGrupo() != null ? u.getGrupo().getId() : null)
                .orElse(null);
    }

    public Long empresaIdActualOrNull() {
        return usuarioActual()
                .map(u -> u.getGrupo() != null && u.getGrupo().getEmpresa() != null
                        ? u.getGrupo().getEmpresa().getId()
                        : null)
                .orElse(null);
    }

    public Optional<UsuarioDTO> usuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        return usuarioService.getUsuarioByUsername(auth.getName());
    }
}
