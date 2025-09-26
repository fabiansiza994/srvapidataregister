package com.fmsp.srvapidataregister.modules.groups.service.impl;

import com.fmsp.srvapidataregister.core.exceptions.CustomServiceException;
import com.fmsp.srvapidataregister.modules.auth.service.PermisoService;
import com.fmsp.srvapidataregister.modules.companies.repository.EmpresaRepository;
import com.fmsp.srvapidataregister.modules.groups.dto.*;
import com.fmsp.srvapidataregister.modules.groups.entity.Grupo;
import com.fmsp.srvapidataregister.modules.groups.repository.GrupoRepository;
import com.fmsp.srvapidataregister.modules.groups.service.IGrupoService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GrupoService implements IGrupoService {

    private final EmpresaRepository empresaRepository;
    private final GrupoRepository grupoRepository;
    private final ModelMapper modelMapper;
    private final PermisoService permisoService;

    public GrupoService(EmpresaRepository empresaRepository, GrupoRepository grupoRepository, ModelMapper modelMapper, PermisoService permisoService) {
        this.empresaRepository = empresaRepository;
        this.grupoRepository = grupoRepository;
        this.modelMapper = modelMapper;
        this.permisoService = permisoService;
    }

    @Override
    public GrupoDTO save(GrupoDTO grupoDTO) {
        var grupo = modelMapper.map(grupoDTO, Grupo.class);
        return modelMapper.map(grupoRepository.save(grupo), GrupoDTO.class);
    }

    @Override
    @Transactional
    public GrupoDetailDTO create(GrupoCreateDTO dto, String idTx) {
        // Solo ADMIN crea grupos dentro de su empresa
        if (!permisoService.hasRole("ADMIN")) {
            throw new CustomServiceException(idTx, "E003", "No autorizado para crear grupos");
        }
        Long empresaIdActual = permisoService.empresaIdActualOrNull();
        if (empresaIdActual == null) throw new CustomServiceException(idTx, "E003", "Empresa no asociada");

        if (!empresaIdActual.equals(dto.getEmpresaId())) {
            throw new CustomServiceException(idTx, "E003", "No puedes crear grupos en otra empresa");
        }

        var empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new CustomServiceException(idTx, "E003", "Empresa no encontrada"));

        if (grupoRepository.existsByNombreIgnoreCaseAndEmpresa_Id(dto.getNombre(), empresa.getId())) {
            throw new CustomServiceException(idTx, "E409", "Ya existe un grupo con ese nombre en la empresa");
        }

        Grupo g = new Grupo();
        g.setNombre(dto.getNombre());
        g.setEmpresa(empresa);

        var saved = grupoRepository.save(g);
        return toDetail(saved);
    }

    @Override
    @Transactional
    public GrupoDetailDTO update(Long id, GrupoUpdateDTO dto, String idTx) {
        if (!permisoService.hasRole("ADMIN")) {
            throw new CustomServiceException(idTx, "E003", "No autorizado para actualizar grupos");
        }
        Long empresaIdActual = permisoService.empresaIdActualOrNull();
        if (empresaIdActual == null) throw new CustomServiceException(idTx, "E003", "Empresa no asociada");

        var g = grupoRepository.findById(id)
                .orElseThrow(() -> new CustomServiceException(idTx, "E404", "Grupo no encontrado"));

        Long empresaGrupo = g.getEmpresa() != null ? g.getEmpresa().getId() : null;
        if (empresaGrupo == null || !empresaIdActual.equals(empresaGrupo)) {
            throw new CustomServiceException(idTx, "E003", "El grupo no pertenece a tu empresa");
        }

        if (grupoRepository.existsByNombreIgnoreCaseAndEmpresa_IdAndIdNot(
                dto.getNombre(), empresaGrupo, g.getId())) {
            throw new CustomServiceException(idTx, "E409", "Ya existe otro grupo con ese nombre en la empresa");
        }

        g.setNombre(dto.getNombre());
        var updated = grupoRepository.save(g);
        return toDetail(updated);
    }

    @Override
    @Transactional
    public void delete(Long id, String idTx) {
        if (!permisoService.hasRole("ADMIN")) {
            throw new CustomServiceException(idTx, "E003", "No autorizado para eliminar grupos");
        }
        Long empresaIdActual = permisoService.empresaIdActualOrNull();
        if (empresaIdActual == null) throw new CustomServiceException(idTx, "E003", "Empresa no asociada");

        var g = grupoRepository.findById(id)
                .orElseThrow(() -> new CustomServiceException(idTx, "E404", "Grupo no encontrado"));

        Long empresaGrupo = g.getEmpresa() != null ? g.getEmpresa().getId() : null;
        if (empresaGrupo == null || !empresaIdActual.equals(empresaGrupo)) {
            throw new CustomServiceException(idTx, "E003", "El grupo no pertenece a tu empresa");
        }

        // Regla de negocio: si tiene usuarios, bloquear borrado
        if (g.getUsuarios() != null && !g.getUsuarios().isEmpty()) {
            throw new CustomServiceException(idTx, "E409",
                    "No se puede eliminar: el grupo tiene usuarios asociados (" + g.getUsuarios().size() + ")");
        }

        grupoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GrupoListDTO> list(int page, int size, String sortBy, String direction) {
        String idTx = UUID.randomUUID().toString();
        Sort sort = "DESC".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Long empresaId = permisoService.empresaIdActualOrNull();
        if (empresaId == null) throw new CustomServiceException(idTx, "E003", "Empresa no asociada");

        Page<Grupo> pageData = grupoRepository.findAllByEmpresa_Id(empresaId, pageable);
        return pageData.map(this::toList);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GrupoListDTO> search(String q, int page, int size, String sortBy, String direction) {
        String idTx = UUID.randomUUID().toString();
        if (q == null || q.trim().isEmpty()) {
            return list(page, size, sortBy, direction);
        }
        Sort sort = "DESC".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Long empresaId = permisoService.empresaIdActualOrNull();
        if (empresaId == null) throw new CustomServiceException(idTx, "E003", "Empresa no asociada");

        return grupoRepository.searchByEmpresa(empresaId, q.trim(), pageable)
                .map(this::toList);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<GrupoDetailDTO> findDetail(Long id, String idTx) {
        var opt = grupoRepository.findById(id);
        if (opt.isEmpty()) return java.util.Optional.empty();

        var g = opt.get();
        Long empresaId = permisoService.empresaIdActualOrNull();
        Long empresaGrupo = g.getEmpresa() != null ? g.getEmpresa().getId() : null;

        if (empresaId == null || empresaGrupo == null || !empresaId.equals(empresaGrupo)) {
            throw new CustomServiceException(idTx, "E003", "El grupo no pertenece a tu empresa");
        }

        return java.util.Optional.of(toDetail(g));
    }

    // ====== mapeos ======
    private GrupoListDTO toList(Grupo g) {
        GrupoListDTO dto = new GrupoListDTO();
        dto.setId(g.getId());
        dto.setNombre(g.getNombre());
        if (g.getEmpresa() != null) {
            dto.setEmpresaId(g.getEmpresa().getId());
            dto.setEmpresaNombre(g.getEmpresa().getNombre());
        }
        dto.setUsuariosCount(g.getUsuarios() == null ? 0 : g.getUsuarios().size());
        return dto;
    }

    private GrupoDetailDTO toDetail(Grupo g) {
        GrupoDetailDTO dto = new GrupoDetailDTO();
        dto.setId(g.getId());
        dto.setNombre(g.getNombre());
        if (g.getEmpresa() != null) {
            dto.setEmpresaId(g.getEmpresa().getId());
            dto.setEmpresaNombre(g.getEmpresa().getNombre());
        }
        if (g.getUsuarios() != null) {
            dto.setUsuarios(g.getUsuarios().stream()
                    .map(u -> modelMapper.map(u, com.fmsp.srvapidataregister.modules.users.dto.UsuarioDTO.class))
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
