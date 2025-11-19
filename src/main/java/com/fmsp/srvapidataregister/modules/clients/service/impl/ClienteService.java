package com.fmsp.srvapidataregister.modules.clients.service.impl;

import com.fmsp.srvapidataregister.core.exceptions.CustomServiceException;
import com.fmsp.srvapidataregister.modules.auth.service.PermisoService;
import com.fmsp.srvapidataregister.modules.clients.dto.*;
import com.fmsp.srvapidataregister.modules.clients.entity.Cliente;
import com.fmsp.srvapidataregister.modules.clients.repository.ClienteRepository;
import com.fmsp.srvapidataregister.modules.clients.service.IClienteService;
import com.fmsp.srvapidataregister.modules.companies.service.ICompanyService;
import com.fmsp.srvapidataregister.modules.jobs.repository.TrabajoRepository;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import com.fmsp.srvapidataregister.modules.paciente.service.IPacienteService;
import com.fmsp.srvapidataregister.modules.users.service.IUsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ClienteService implements IClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    private final TrabajoRepository trabajoRepository;
    private final IUsuarioService usuarioService;
    private final IPacienteService pacienteService;
    private final PermisoService permisoService;
    private final ICompanyService companyService;
    private final com.fmsp.srvapidataregister.modules.S3.service.S3Service s3Service;

    public ClienteService(ClienteRepository clienteRepository, ModelMapper modelMapper, TrabajoRepository trabajoRepository, IUsuarioService usuarioService, IPacienteService pacienteService, PermisoService permisoService, ICompanyService companyService, com.fmsp.srvapidataregister.modules.S3.service.S3Service s3Service) {
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
        this.trabajoRepository = trabajoRepository;
        this.usuarioService = usuarioService;
        this.pacienteService = pacienteService;
        this.permisoService = permisoService;
        this.companyService = companyService;
        this.s3Service = s3Service;
    }

    @Override
    public ClienteResponseDTO createClient(ClienteDTO clienteDTO, String uuid) {
        var usuario = usuarioService.getUsuarioById(clienteDTO.getUsuario().getId());
        if (usuario.isEmpty()) {
            throw new CustomServiceException(uuid, "E003", "Usuario no encontrado");
        }
        if (usuario.get().getGrupo().getEmpresa().getSector().getNombre().equalsIgnoreCase("SALUD")) {
            if (!clienteDTO.getPacientes().isEmpty()) {
                var pacienteList = new ArrayList<PacienteDTO>();
                createPatientList(clienteDTO, pacienteList);
                guardarPaciente(pacienteList);
                clienteDTO.setPacientes(pacienteList);
            }
        }
        var client = modelMapper.map(clienteDTO, Cliente.class);
        var clienteDB = clienteRepository.save(client);

        clienteDTO.setId(clienteDB.getId());
        return modelMapper.map(clienteDTO, ClienteResponseDTO.class);
    }

    private void createPatientList(ClienteDTO clienteDTO, ArrayList<PacienteDTO> pacienteList) {
        clienteDTO.getPacientes().forEach(pacienteDTO -> {
            var paciente = new PacienteDTO();
            paciente.setId(pacienteDTO.getId());
            paciente.setNombre(pacienteDTO.getNombre());
            paciente.setEmail(pacienteDTO.getEmail());
            paciente.setTelefono(pacienteDTO.getTelefono());
            paciente.setDireccion(pacienteDTO.getDireccion());
            pacienteList.add(paciente);
        });
    }

    private void guardarPaciente(ArrayList<PacienteDTO> pacienteList) {
        pacienteList.forEach(pacienteService::save);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<ClientePlanoDTO> listarClientes(int page, int size, String sortBy, String direction) {
        Sort sort = "DESC".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Long empresaId = permisoService.empresaIdActualOrNull();
        if (empresaId == null) throw new AccessDeniedException("Empresa no asociada");

        var allowed = companyService.getSettings(empresaId, "123");

        if (allowed.getAllowView() || permisoService.hasRole("ADMIN")) {
            Page<Cliente> pageClientes = clienteRepository.findAllByEmpresa_Id(empresaId, pageable);
            return pageClientes.map(c -> modelMapper.map(c, ClientePlanoDTO.class));
        }

        if (permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            if (grupoId == null) throw new AccessDeniedException("Grupo no asociado");
            Page<Cliente> pageClientes = clienteRepository.findAllByUsuario_Grupo_Id(grupoId, pageable);
            return pageClientes.map(c -> modelMapper.map(c, ClientePlanoDTO.class));
        }

        throw new AccessDeniedException("Rol no permitido");
    }

    @Override
    public Optional<ClientePlanoDTO> findById(Long id) {
        var cliente = clienteRepository.findById(id);
        return cliente.map(c -> modelMapper.map(c, ClientePlanoDTO.class));
    }

    @Override
    public Cliente findClienteById(Long id) {
        return clienteRepository.findById(id).orElseThrow(() -> new CustomServiceException("123", "E404", "Cliente no encontrado"));
    }

    @Override
    public Page<ClientePlanoDTO> searchClientes(String q, int page, int size, String sortBy, String direction) {
        if (q == null || q.trim().isEmpty()) {
            // Si no mandan criterio, delega a listarClientes con los mismos params
            return listarClientes(page, size, sortBy, direction);
        }

        Sort sort = "DESC".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (permisoService.hasRole("ADMIN")) {
            Long empresaId = permisoService.empresaIdActualOrNull();
            if (empresaId == null) throw new AccessDeniedException("Empresa no asociada");
            Page<Cliente> pageClientes = clienteRepository.searchByEmpresa(empresaId, q.trim(), pageable);
            return pageClientes.map(c -> modelMapper.map(c, ClientePlanoDTO.class));
        }

        if (permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            if (grupoId == null) throw new AccessDeniedException("Grupo no asociado");
            Page<Cliente> pageClientes = clienteRepository.searchByGrupo(grupoId, q.trim(), pageable);
            return pageClientes.map(c -> modelMapper.map(c, ClientePlanoDTO.class));
        }

        throw new AccessDeniedException("Rol no permitido");
    }

    @Transactional
    public void deleteCliente(Long clienteId, String uuid) {

        var opt = clienteRepository.findById(clienteId);
        if (opt.isEmpty()) {
            throw new CustomServiceException(uuid, "E404", "Cliente no encontrado");
        }
        var cliente = opt.get();


        Long empresaId = permisoService.empresaIdActualOrNull();
        var allowed = companyService.getSettings(empresaId, "123");

        // 1) Alcance por rol
        if (allowed.getAllowEdit() || permisoService.hasRole("ADMIN")) {

            if (empresaId == null || cliente.getEmpresa() == null || !empresaId.equals(cliente.getEmpresa().getId())) {
                throw new AccessDeniedException("El cliente no pertenece a tu empresa");
            }
        } else if (allowed.getAllowEdit() || permisoService.hasRole("USER")) {

            if(!opt.get().getUsuario().getId().equals(permisoService.usuarioActual().get().getId())){
                throw new CustomServiceException(uuid, "E404", "el cliente fue creado  por otro usuario");
            }

            Long grupoId = permisoService.grupoIdActualOrNull();
            Long grupoCliente = (cliente.getUsuario() != null && cliente.getUsuario().getGrupo() != null)
                    ? cliente.getUsuario().getGrupo().getId() : null;
            if (grupoId == null || !grupoId.equals(grupoCliente)) {
                throw new AccessDeniedException("El cliente no pertenece a tu grupo");
            }

        } else {
            throw new AccessDeniedException("Rol no permitido");
        }

        long trabajos = trabajoRepository.countByCliente_Id(clienteId);
        if (trabajos > 0) {
            throw new CustomServiceException(uuid, "E409",
                    "No se puede eliminar: el cliente tiene trabajos asociados (" + trabajos + ")");
        }

        long pacientes = pacienteService.countByCliente(clienteId);
        if (pacientes > 0) {
            throw new CustomServiceException(uuid, "E409",
                    "No se puede eliminar: el cliente tiene pacientes asociados (" + pacientes + ")");
        }

        clienteRepository.deleteById(clienteId);
    }

    @Transactional(readOnly = true)
    public ClienteDetailDTO getClienteDetail(Long clienteId, String uuid) {
        var clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new CustomServiceException(uuid, "E404", "Cliente no encontrado");
        }

        var cliente = clienteOpt.get();
        var clienteDetail = modelMapper.map(cliente, ClienteDetailDTO.class);

        var pacientes = pacienteService.findByCliente(clienteId);
        clienteDetail.setPacientes(pacientes);

        return clienteDetail;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Transactional
    public ClienteResponseDTO updateCliente(Long clienteId, ClienteUpdateDTO dto, String uuid) {
        if(dto.getEmail().isEmpty()){
            throw new CustomServiceException(uuid, "E404", "email requerido");
        }
        var clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new CustomServiceException(uuid, "E404", "Cliente no encontrado");
        }

        var cliente = clienteOpt.get();

        Long empresaId = permisoService.empresaIdActualOrNull();
        var allowed = companyService.getSettings(empresaId, "123");

        if (allowed.getAllowEdit() || permisoService.hasRole("ADMIN")) {

            if (empresaId == null || cliente.getEmpresa() == null || !empresaId.equals(cliente.getEmpresa().getId())) {
                throw new AccessDeniedException("El cliente no pertenece a tu empresa");
            }

            if (dto.getIdentificacion() != null && !dto.getIdentificacion().equalsIgnoreCase(cliente.getIdentificacion())) {
                boolean yaExiste = clienteRepository.existsByIdentificacionAndEmpresa_IdAndIdNot(
                        dto.getIdentificacion(), empresaId, cliente.getId());
                if (yaExiste) {
                    throw new CustomServiceException(uuid, "E409",
                            "Ya existe un cliente con la misma identificación en tu empresa");
                }
            }

        } else if (allowed.getAllowEdit() || permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            Long grupoCliente = (cliente.getUsuario() != null && cliente.getUsuario().getGrupo() != null)
                    ? cliente.getUsuario().getGrupo().getId() : null;

            if (grupoId == null || !grupoId.equals(grupoCliente)) {
                throw new AccessDeniedException("El cliente no pertenece a tu grupo");
            }

            if (dto.getIdentificacion() != null && !dto.getIdentificacion().equalsIgnoreCase(cliente.getIdentificacion())) {
                Long empresaDelCliente = (cliente.getEmpresa() != null) ? cliente.getEmpresa().getId() : null;
                if (empresaDelCliente != null) {
                    boolean yaExiste = clienteRepository.existsByIdentificacionAndEmpresa_IdAndIdNot(
                            dto.getIdentificacion(), empresaDelCliente, cliente.getId());
                    if (yaExiste) {
                        throw new CustomServiceException(uuid, "E409",
                                "Ya existe un cliente con la misma identificación en tu empresa");
                    }
                }
            }

        } else {
            throw new AccessDeniedException("Rol no permitido");
        }

        // ===== Actualización de campos básicos =====
        cliente.setIdentificacion(dto.getIdentificacion());
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setEmail(dto.getEmail());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTelefono(dto.getTelefono());

        // ===== Manejo de razonSocial (siempre se actualiza) =====
        cliente.setRazonSocial(dto.getRazonSocial());

        // ===== Manejo inteligente de documentos =====
        // camaraComercio
        if (dto.getCamaraComercio() == null || dto.getCamaraComercio().isBlank()) {
            // null o "" => eliminar referencia al doc
            cliente.setCamaraComercio(null);
        } else if (!dto.getCamaraComercio().equals(cliente.getCamaraComercio())) {
            // viene un valor distinto (nueva URL) => la guardamos
            cliente.setCamaraComercio(dto.getCamaraComercio());
        }
        // Si es exactamente igual, no se toca.

        // rut
        if (dto.getRut() == null || dto.getRut().isBlank()) {
            cliente.setRut(null);
        } else if (!dto.getRut().equals(cliente.getRut())) {
            cliente.setRut(dto.getRut());
        }

        cliente.setEstado(dto.getEstado());
        var actualizado = clienteRepository.save(cliente);
        return modelMapper.map(actualizado, ClienteResponseDTO.class);
    }

    public String uploadDocIfPresent(String keyPrefix, MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) return null;
            byte[] bytes = file.getBytes();
            String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            return s3Service.uploadFile(keyPrefix, bytes, contentType);
        } catch (Exception e) {
            try {
                return s3Service.uploadFile(keyPrefix, file.getBytes(), "application/octet-stream");
            } catch (Exception ex) {
                throw new RuntimeException("Error subiendo documento", ex);
            }
        }
    }
}
