package com.fmsp.srvapidataregister.modules.users.service.impl;

import com.fmsp.srvapidataregister.core.exceptions.CustomServiceException;
import com.fmsp.srvapidataregister.core.exceptions.InternalServerException;
import com.fmsp.srvapidataregister.modules.auth.service.PermisoService;
import com.fmsp.srvapidataregister.modules.companies.dto.EmpresaDTO;
import com.fmsp.srvapidataregister.modules.companies.dto.EmpresaLiteDTO;
import com.fmsp.srvapidataregister.modules.companies.entity.Empresa;
import com.fmsp.srvapidataregister.modules.companies.service.IEmpresaService;
import com.fmsp.srvapidataregister.modules.groups.dto.GrupoDTO;
import com.fmsp.srvapidataregister.modules.groups.dto.GrupoLiteDTO;
import com.fmsp.srvapidataregister.modules.groups.entity.Grupo;
import com.fmsp.srvapidataregister.modules.groups.repository.GrupoRepository;
import com.fmsp.srvapidataregister.modules.pais.service.IPaisService;
import com.fmsp.srvapidataregister.modules.roles.dto.RolDTO;
import com.fmsp.srvapidataregister.modules.roles.dto.RolLiteDTO;
import com.fmsp.srvapidataregister.modules.roles.entity.Rol;
import com.fmsp.srvapidataregister.modules.roles.service.IRolService;
import com.fmsp.srvapidataregister.modules.sector.service.ISectorService;
import com.fmsp.srvapidataregister.modules.users.dto.*;
import com.fmsp.srvapidataregister.modules.users.entity.Usuario;
import com.fmsp.srvapidataregister.modules.users.repository.UsuarioRepository;
import com.fmsp.srvapidataregister.modules.users.service.IUsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    private final IRolService rolService;
    private final IEmpresaService empresaService;
    private final GrupoRepository grupoRepository;
    private final ISectorService sectorService;
    private final IPaisService paisService;

    private final PermisoService permisoService;

    public UsuarioService(UsuarioRepository usuarioRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder,
                          IRolService rolService, IEmpresaService empresaService, GrupoRepository grupoRepository,
                          ISectorService sectorService, IPaisService paisService, PermisoService permisoService) {
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.rolService = rolService;
        this.empresaService = empresaService;
        this.grupoRepository = grupoRepository;
        this.sectorService = sectorService;
        this.paisService = paisService;
        this.permisoService = permisoService;
    }

    @Override
    public Optional<UsuarioDTO> getUsuarioById(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(u -> modelMapper.map(u, UsuarioDTO.class));
    }

    @Override
    public Optional<UsuarioDTO> getUsuarioByUsername(String username) {
        Optional<Usuario> user = usuarioRepository.findByUsuarioWithRelations(username);

        if (user.isEmpty()) {
            throw new CustomServiceException("123", "E003", "usuario o clave incorrecto :/");
        }

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(user.get().getId());
        usuarioDTO.setUsuario(user.get().getUsuario());
        usuarioDTO.setNombre(user.get().getNombre());
        usuarioDTO.setApellido(user.get().getApellido());
        usuarioDTO.setPassword(user.get().getPassword());
        usuarioDTO.setEmail(user.get().getEmail());

        usuarioDTO.setBloqueado(user.get().isBloqueado());
        usuarioDTO.setIntentosFallidos(user.get().getIntentosFallidos());
        usuarioDTO.setBloqueado(user.get().isBloqueado());
        return Optional.of(usuarioDTO);
    }

    @Override
    public UsuarioDTO saveUsuario(UsuarioDTO user) {
        var userDb = usuarioRepository.save(modelMapper.map(user, Usuario.class));
        return modelMapper.map(userDb, UsuarioDTO.class);
    }

    @Transactional
    @Override
    public UsuarioDTO registerUsuario(RegistroDTO registroDTO, String uuid) {
        if (getUsuarioByEmail(registroDTO.getEmail())) {
            throw new InternalServerException(uuid, "E001", "El usuario ya existe.");
        }

        var sector = sectorService.findSectorById(registroDTO.getEmpresa().getSector().getId());
        if (sector == null) {
            throw new InternalServerException(uuid, "E001", "El sector no existe.");
        }

        var pais = paisService.findById(registroDTO.getEmpresa().getPais().getId());
        if (pais == null) {
            throw new InternalServerException(uuid, "E001", "El pais no existe.");
        }

        EmpresaDTO empresaGuardada = null;
        var empresaDB = empresaService.findByNombre(registroDTO.getEmpresa().getNombre());
        if (empresaDB == null) {
            EmpresaDTO empresaDTO = modelMapper.map(registroDTO.getEmpresa(), EmpresaDTO.class);
            empresaDTO.setPais(pais);
            empresaDTO.setSector(sector);
            empresaDTO.setEstado("ACTIVO");
            empresaGuardada = empresaService.save(empresaDTO);
        } else {
            empresaGuardada = empresaDB;
        }

        RolDTO rol;
        if (registroDTO.getRol() == null || registroDTO.getRol().getId() == null) {
            rol = rolService.findById(1L);
        } else {
            rol = rolService.findById(registroDTO.getRol().getId());
        }

        Grupo grupoGuardado;
        if (registroDTO.getGrupo().getId() == null && registroDTO.getGrupo().getNombre() != null) {
            Grupo grupo = new Grupo();
            grupo.setNombre(registroDTO.getGrupo().getNombre());
            grupo.setEmpresa(modelMapper.map(empresaGuardada, Empresa.class));
            grupoGuardado = grupoRepository.save(grupo);
        } else {
            var grupoDB = grupoRepository.findById(registroDTO.getGrupo().getId());
            grupoGuardado = grupoDB.get();
        }

        UsuarioDTO usuarioDTO = modelMapper.map(registroDTO, UsuarioDTO.class);
        usuarioDTO.setUsuario(registroDTO.getUsuario());
        usuarioDTO.setRol(rol);
        usuarioDTO.setGrupo(modelMapper.map(grupoGuardado, GrupoDTO.class));
        usuarioDTO.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));

        usuarioDTO.setUsuario(usuarioDTO.getUsuario().toLowerCase().trim());

        var mapper = modelMapper.map(usuarioDTO, Usuario.class);
        usuarioRepository.save(mapper);
        registroDTO.setRol(rol);
        return modelMapper.map(registroDTO, UsuarioDTO.class);
    }

    public boolean getUsuarioByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Page<UsuarioListDTO> list(int page, int size, String sortBy, String direction) {
        String uuid = UUID.randomUUID().toString();
        Sort sort = "DESC".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (permisoService.hasRole("ADMIN")) {
            Long empresaId = permisoService.empresaIdActualOrNull();
            if (empresaId == null) throw new CustomServiceException(uuid, "E404", "Empresa no asociada");
            return usuarioRepository.findAllByGrupo_Empresa_Id(empresaId, pageable)
                    .map(this::toListDTO);
        }
        if (permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            if (grupoId == null) throw new CustomServiceException(uuid, "E404", "Grupo no asociado");
            return usuarioRepository.findAllByGrupo_Id(grupoId, pageable)
                    .map(this::toListDTO);
        }
        throw new CustomServiceException(uuid, "E404", "Rol no permitido");
    }

    // ===== SEARCH =====
    public Page<UsuarioListDTO> search(String q, int page, int size, String sortBy, String direction) {
        String uuid = UUID.randomUUID().toString();
        if (q == null || q.trim().isEmpty()) {
            return list(page, size, sortBy, direction);
        }
        Sort sort = "DESC".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        String query = q.trim();

        if (permisoService.hasRole("ADMIN")) {
            Long empresaId = permisoService.empresaIdActualOrNull();
            if (empresaId == null) throw new CustomServiceException(uuid, "E404", "Empresa no asociada");
            return usuarioRepository.searchByEmpresa(empresaId, query, pageable)
                    .map(this::toListDTO);
        }
        if (permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            if (grupoId == null) throw new CustomServiceException(uuid, "E404", "Grupo no asociado");
            return usuarioRepository.searchByGrupo(grupoId, query, pageable)
                    .map(this::toListDTO);
        }
        throw new CustomServiceException(uuid, "E404", "Rol no permitido");
    }

    // ===== DETAIL =====
    public UsuarioDetailDTO detail(Long id, String uuid) {
        var opt = usuarioRepository.fetchDetail(id);
        if (opt.isEmpty()) throw new CustomServiceException(uuid, "E404", "Usuario no encontrado");
        var u = opt.get();

        if (permisoService.hasRole("ADMIN")) {
            Long empresaId = permisoService.empresaIdActualOrNull();
            Long empresaUsuario = (u.getGrupo() != null && u.getGrupo().getEmpresa() != null)
                    ? u.getGrupo().getEmpresa().getId() : null;
            if (empresaId == null || empresaUsuario == null || !empresaId.equals(empresaUsuario)) {
                throw new CustomServiceException(uuid, "E404", "El usuario no pertenece a tu empresa");
            }
        } else if (permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            Long grupoUsuario = (u.getGrupo() != null) ? u.getGrupo().getId() : null;
            if (grupoId == null || !grupoId.equals(grupoUsuario)) {
                throw new CustomServiceException(uuid, "E404", "El usuario no pertenece a tu grupo");
            }
        } else {
            throw new CustomServiceException(uuid, "E404", "Rol no permitido");
        }
        return toDetailDTO(u);
    }

    // ===== UPDATE (solo ADMIN) =====
    @Transactional
    public UsuarioDetailDTO update(Long id, UsuarioUpdateDTO dto, String uuid) {
        if (!permisoService.hasRole("ADMIN")) {
            throw new CustomServiceException(uuid, "E404", "No autorizado para actualizar usuarios");
        }
        var u = usuarioRepository.findById(id)
                .orElseThrow(() -> new CustomServiceException(uuid, "E404", "Usuario no encontrado"));

        Long empresaId = permisoService.empresaIdActualOrNull();
        Long empresaUsuario = (u.getGrupo() != null && u.getGrupo().getEmpresa() != null)
                ? u.getGrupo().getEmpresa().getId() : null;
        if (empresaId == null || empresaUsuario == null || !empresaId.equals(empresaUsuario)) {
            throw new CustomServiceException(uuid, "E404", "El usuario no pertenece a tu empresa");
        }

        // validar duplicados (mismo dominio empresa)
        if (usuarioRepository.existsByEmailAndGrupo_Empresa_IdAndIdNot(dto.getEmail(), empresaId, u.getId())) {
            throw new CustomServiceException(uuid, "E409", "Ya existe un usuario con ese email en tu empresa");
        }

        // actualizar básicos
        u.setNombre(dto.getNombre());
        u.setApellido(dto.getApellido());
        u.setEmail(dto.getEmail());

        // opcionales (solo si vienen)
        if (dto.getBloqueado() != null) u.setBloqueado(dto.getBloqueado());
        if (dto.getIntentosFallidos() != null) u.setIntentosFallidos(dto.getIntentosFallidos());

        // cambio de grupo (solo ADMIN)
        if (dto.getGrupoId() != null) {
            var g = grupoRepository.findById(dto.getGrupoId())
                    .orElseThrow(() -> new CustomServiceException(uuid, "E003", "Grupo no encontrado"));
            // grupo debe ser de la misma empresa del admin
            Long empresaGrupo = (g.getEmpresa() != null) ? g.getEmpresa().getId() : null;
            if (!empresaId.equals(empresaGrupo)) {
                throw new CustomServiceException(uuid, "E404", "No puedes mover el usuario a un grupo de otra empresa");
            }
            u.setGrupo(g);
        }

        // cambio de rol (solo ADMIN)
        if (dto.getRolId() != null) {
            var rol = rolService.findById(dto.getRolId());
            if (rol == null || rol.getId() == null) {
                throw new CustomServiceException(uuid, "E003", "Rol no encontrado");
            }
            u.setRol(modelMapper.map(rol, Rol.class));
        }

        var saved = usuarioRepository.save(u);
        return toDetailDTO(saved);
    }

    // ===== DELETE (solo ADMIN) =====
    @Transactional
    public void delete(Long id, String uuid) {
        if (!permisoService.hasRole("ADMIN")) {
            throw new CustomServiceException(uuid, "E404", "No autorizado para eliminar usuarios");
        }
        var u = usuarioRepository.findById(id)
                .orElseThrow(() -> new CustomServiceException(uuid, "E404", "Usuario no encontrado"));

        Long empresaId = permisoService.empresaIdActualOrNull();
        Long empresaUsuario = (u.getGrupo() != null && u.getGrupo().getEmpresa() != null)
                ? u.getGrupo().getEmpresa().getId() : null;
        if (empresaId == null || empresaUsuario == null || !empresaId.equals(empresaUsuario)) {
            throw new CustomServiceException(uuid, "E404", "El usuario no pertenece a tu empresa");
        }

        // (opcional) impedir que un ADMIN se elimine a sí mismo, o que elimine al único ADMIN de la empresa, etc.

        usuarioRepository.deleteById(id);
    }

    @Override
    public UserProfileDTO profile(Long id, String uuid) {
        var opt = usuarioRepository.fetchDetailMenu(id);
        if (opt.isEmpty()) throw new CustomServiceException(uuid, "E404", "Usuario no encontrado");
        var u = opt.get();

        // ===== Autorización (igual filosofía que detail) =====
        if (permisoService.hasRole("ADMIN")) {
            Long empresaId = permisoService.empresaIdActualOrNull();
            Long empresaUsuario = (u.getGrupo() != null && u.getGrupo().getEmpresa() != null)
                    ? u.getGrupo().getEmpresa().getId() : null;
            if (empresaId == null || empresaUsuario == null || !empresaId.equals(empresaUsuario)) {
                throw new CustomServiceException(uuid, "E404", "El usuario no pertenece a tu empresa");
            }
        } else if (permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            Long grupoUsuario = (u.getGrupo() != null) ? u.getGrupo().getId() : null;
            if (grupoId == null || !grupoId.equals(grupoUsuario)) {
                throw new CustomServiceException(uuid, "E404", "El usuario no pertenece a tu grupo");
            }
        } else {
            throw new CustomServiceException(uuid, "E404", "Rol no permitido");
        }

        return toProfileDTO(u);
    }

    @Override
    public int updateBlockValue(int value, Boolean isBlocked, Long id) {
        return usuarioRepository.updateBlockValue(value, isBlocked, id);
    }

    private UserProfileDTO toProfileDTO(Usuario u) {
        UserProfileDTO d = new UserProfileDTO();
        d.setId(u.getId());
        d.setNombre(u.getNombre());
        d.setApellido(u.getApellido());
        d.setUsuario(u.getUsuario());
        d.setEmail(u.getEmail());
        d.setIntentosFallidos(u.getIntentosFallidos());
        d.setBloqueado(u.isBloqueado());

        if (u.getRol() != null) {
            RolLiteDTO r = new RolLiteDTO();
            r.setId(u.getRol().getId());
            r.setNombre(u.getRol().getNombre());
            d.setRol(r);
        }

        if (u.getGrupo() != null) {
            GrupoLiteDTO g = new GrupoLiteDTO();
            g.setId(u.getGrupo().getId());
            g.setNombre(u.getGrupo().getNombre());
            d.setGrupo(g);

            if (u.getGrupo().getEmpresa() != null) {
                var e = u.getGrupo().getEmpresa();
                EmpresaLiteDTO emp = new EmpresaLiteDTO();
                emp.setId(e.getId());
                emp.setNombre(e.getNombre());
                // Campos opcionales si están cargados en el grafo:
                emp.setSectorNombre(e.getSector() != null ? e.getSector().getNombre() : null);
                emp.setPaisNombre(e.getPais() != null ? e.getPais().getNombre() : null);
                d.setEmpresa(emp);
            }
        }

        return d;
    }

    // ====== mapeos ======
    private UsuarioListDTO toListDTO(Usuario u) {
        UsuarioListDTO d = new UsuarioListDTO();
        d.setId(u.getId());
        d.setNombre(u.getNombre());
        d.setApellido(u.getApellido());
        d.setUsuario(u.getUsuario());
        d.setEmail(u.getEmail());
        if (u.getGrupo() != null) {
            d.setGrupoId(u.getGrupo().getId());
            d.setGrupoNombre(u.getGrupo().getNombre());
        }
        if (u.getRol() != null) d.setRolNombre(u.getRol().getNombre());
        d.setBloqueado(u.isBloqueado());
        return d;
    }

    private UsuarioDetailDTO toDetailDTO(Usuario u) {
        UsuarioDetailDTO d = new UsuarioDetailDTO();
        d.setId(u.getId());
        d.setNombre(u.getNombre());
        d.setApellido(u.getApellido());
        d.setUsuario(u.getUsuario());
        d.setEmail(u.getEmail());
        if (u.getGrupo() != null) {
            d.setGrupoId(u.getGrupo().getId());
            d.setGrupoNombre(u.getGrupo().getNombre());
        }
        if (u.getRol() != null) {
            d.setRolId(u.getRol().getId());
            d.setRolNombre(u.getRol().getNombre());
        }
        d.setIntentosFallidos(u.getIntentosFallidos());
        d.setBloqueado(u.isBloqueado());
        return d;
    }
}