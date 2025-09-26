package com.fmsp.srvapidataregister.modules.auth.service;

import com.fmsp.srvapidataregister.modules.clients.repository.ClienteRepository;
import com.fmsp.srvapidataregister.modules.users.dto.UsuarioDTO;
import com.fmsp.srvapidataregister.modules.users.entity.Usuario;
import com.fmsp.srvapidataregister.modules.users.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("permisoService") // asegura el nombre del bean usado en SpEL: @permisoService
@Transactional(readOnly = true)
public class PermisoService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    public PermisoService(UsuarioRepository usuarioRepository, ClienteRepository clienteRepository, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
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

    public Optional<UsuarioDTO> getUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();

        // Aquí asumo que tu usuario se identifica por username en el Authentication
        String username = auth.getName();
        Optional<Usuario> user = usuarioRepository.findByUsuarioWithRelations(username);
        return user.map(u -> modelMapper.map(u, UsuarioDTO.class));
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

        Optional<Usuario> user = usuarioRepository.findByUsuarioWithRelations(auth.getName());
        return user.map(u -> modelMapper.map(u, UsuarioDTO.class));
    }

    // Opcional: si quieres devolver un objeto ligero
    public static final class SectorInfo {
        private final Long id;
        private final String nombre;

        public SectorInfo(Long id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
        public Long getId() { return id; }
        public String getNombre() { return nombre; }
    }

    /** Devuelve el ID del sector de la empresa del usuario autenticado, o null si no hay. */
    public Long sectorIdActualOrNull() {
        return usuarioActualEntity()
                .map(u -> {
                    var grupo = u.getGrupo();
                    var empresa = (grupo != null) ? grupo.getEmpresa() : null;
                    var sector = (empresa != null) ? empresa.getSector() : null;
                    return (sector != null) ? sector.getId() : null;
                })
                .orElse(null);
    }

    /** Devuelve el nombre del sector de la empresa del usuario autenticado, o null si no hay. */
    public String sectorNombreActualOrNull() {
        return usuarioActualEntity()
                .map(u -> {
                    var grupo = u.getGrupo();
                    var empresa = (grupo != null) ? grupo.getEmpresa() : null;
                    var sector = (empresa != null) ? empresa.getSector() : null;
                    return (sector != null) ? sector.getNombre() : null;
                })
                .orElse(null);
    }

    /** Devuelve un objeto compacto con id y nombre del sector (o null si no hay). */
    public SectorInfo sectorActualOrNull() {
        return usuarioActualEntity()
                .map(u -> {
                    var grupo = u.getGrupo();
                    var empresa = (grupo != null) ? grupo.getEmpresa() : null;
                    var sector = (empresa != null) ? empresa.getSector() : null;
                    return (sector != null) ? new SectorInfo(sector.getId(), sector.getNombre()) : null;
                })
                .orElse(null);
    }

    /** Helper interno para obtener la entidad Usuario con sus relaciones. */
    private Optional<Usuario> usuarioActualEntity() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        return usuarioRepository.findByUsuarioWithRelations(auth.getName());
    }

}