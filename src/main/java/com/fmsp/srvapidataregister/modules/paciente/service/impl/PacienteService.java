package com.fmsp.srvapidataregister.modules.paciente.service.impl;

import com.fmsp.srvapidataregister.core.exceptions.CustomAccesException;
import com.fmsp.srvapidataregister.core.exceptions.CustomServiceException;
import com.fmsp.srvapidataregister.modules.auth.service.PermisoService;
import com.fmsp.srvapidataregister.modules.clients.entity.Cliente;
import com.fmsp.srvapidataregister.modules.clients.repository.ClienteRepository;
import com.fmsp.srvapidataregister.modules.jobs.repository.TrabajoRepository;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import com.fmsp.srvapidataregister.modules.paciente.entity.Paciente;
import com.fmsp.srvapidataregister.modules.paciente.repository.PacienteRepository;
import com.fmsp.srvapidataregister.modules.paciente.service.IPacienteService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PacienteService implements IPacienteService {

    private final PermisoService permisoService;
    private final PacienteRepository pacienteRepository;
    private final TrabajoRepository trabajoRepository;
    private final ModelMapper modelMapper;
    private final ClienteRepository clienteRepository;

    public PacienteService(PermisoService permisoService, PacienteRepository pacienteRepository, TrabajoRepository trabajoRepository, ModelMapper modelMapper, ClienteRepository clienteRepository) {
        this.permisoService = permisoService;
        this.pacienteRepository = pacienteRepository;
        this.trabajoRepository = trabajoRepository;
        this.modelMapper = modelMapper;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public PacienteDTO save(PacienteDTO pacienteDTO) {
        var paciente = modelMapper.map(pacienteDTO, Paciente.class);
        paciente = pacienteRepository.save(paciente);

        var pacienteDB = modelMapper.map(paciente, PacienteDTO.class);
        if(pacienteDTO.getClienteId() != null) {
            pacienteDB.setClienteId(pacienteDTO.getClienteId());
        }
        return modelMapper.map(pacienteDB, PacienteDTO.class);
    }

    @Override
    public PacienteDTO listPatient(Long clientId) {
        var paciente = pacienteRepository.findByClienteId(clientId);
        paciente = modelMapper.map(paciente, Paciente.class);
        return modelMapper.map(paciente, PacienteDTO.class);
    }

    @Override
    public long countByCliente(Long clienteId) {
        return pacienteRepository.countByClienteId(clienteId);
    }

    @Override
    public Optional<PacienteDTO> findById(Long id) {
        var paciente = pacienteRepository.findById(id);
        return paciente.map(p -> modelMapper.map(p, PacienteDTO.class));
    }

    @Override
    public void deletePaciente(Long pacienteId, String uuid) throws AccessDeniedException {
        var optPac = pacienteRepository.findById(pacienteId);
        if (optPac.isEmpty()) {
            throw new CustomServiceException(uuid, "E404", "Paciente no encontrado");
        }
        var paciente = optPac.get();

        // Resolver Cliente al que pertenece el paciente
        if (paciente.getClienteId() == null) {
            // Si por algún motivo no tiene cliente asociado, negamos por seguridad
            throw new AccessDeniedException("Paciente sin cliente asociado");
        }
        Long clienteId = paciente.getClienteId();
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new CustomServiceException(uuid, "E404", "Cliente del paciente no encontrado"));

        // 1) Alcance por rol (igual a deleteCliente)
        if (permisoService.hasRole("ADMIN")) {
            Long empresaId = permisoService.empresaIdActualOrNull();
            Long empresaClienteId = (cliente.getEmpresa() != null) ? cliente.getEmpresa().getId() : null;
            if (empresaId == null || empresaClienteId == null || !empresaId.equals(empresaClienteId)) {
                throw new CustomAccesException(uuid, "E500", "El paciente no pertenece a tu empresa");
            }
        } else if (permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            Long grupoCliente = (cliente.getUsuario() != null && cliente.getUsuario().getGrupo() != null)
                    ? cliente.getUsuario().getGrupo().getId() : null;
            if (grupoId == null || !grupoId.equals(grupoCliente)) {
                throw new CustomAccesException(uuid, "E500", "El paciente no pertenece a tu grupo");
            }
        } else {
            throw new AccessDeniedException("Rol no permitido");
        }

        // 2) Validación de asociación con trabajos
        long trabajos = trabajoRepository.countByPaciente(pacienteId);
        if (trabajos > 0) {
            throw new CustomServiceException(uuid, "E409",
                    "No se puede eliminar: el paciente tiene trabajos asociados (" + trabajos + ")");
        }

        // 3) Eliminar
        pacienteRepository.deleteById(pacienteId);
    }

    @Override
    public List<PacienteDTO> findByCliente(Long clienteId) {
        var pacientes = pacienteRepository.findAllByClienteId(clienteId);
        return pacientes.stream()
                .map(p -> modelMapper.map(p, PacienteDTO.class))
                .collect(Collectors.toList());
    }

    public Page<PacienteDTO> listarPacientes(int page, int size, String sortBy, String direction) throws AccessDeniedException {
        Sort sort = "DESC".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (permisoService.hasRole("ADMIN")) {
            Long empresaId = permisoService.empresaIdActualOrNull();
            if (empresaId == null) throw new AccessDeniedException("Empresa no asociada");
            return pacienteRepository.findAllByEmpresa(empresaId, pageable)
                    .map(p -> modelMapper.map(p, PacienteDTO.class));
        }

        if (permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            if (grupoId == null) throw new AccessDeniedException("Grupo no asociado");
            return pacienteRepository.findAllByGrupo(grupoId, pageable)
                    .map(p -> modelMapper.map(p, PacienteDTO.class));
        }

        throw new AccessDeniedException("Rol no permitido");
    }

    public Page<PacienteDTO> searchPacientes(String q, int page, int size, String sortBy, String direction) throws AccessDeniedException {
        if (q == null || q.trim().isEmpty()) {
            return listarPacientes(page, size, sortBy, direction);
        }

        Sort sort = "DESC".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        String term = q.trim();

        if (permisoService.hasRole("ADMIN")) {
            Long empresaId = permisoService.empresaIdActualOrNull();
            if (empresaId == null) throw new AccessDeniedException("Empresa no asociada");
            return pacienteRepository.searchByEmpresa(empresaId, term, pageable)
                    .map(p -> modelMapper.map(p, PacienteDTO.class));
        }

        if (permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            if (grupoId == null) throw new AccessDeniedException("Grupo no asociado");
            return pacienteRepository.searchByGrupo(grupoId, term, pageable)
                    .map(p -> modelMapper.map(p, PacienteDTO.class));
        }

        throw new AccessDeniedException("Rol no permitido");
    }

    public Page<PacienteDTO> listarPacientesPorCliente(Long clientId, int page, int size, String sortBy, String direction) {
        Sort sort = "DESC".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return pacienteRepository.findAllByClienteId(clientId, pageable)
                .map(p -> modelMapper.map(p, PacienteDTO.class));
    }
}
