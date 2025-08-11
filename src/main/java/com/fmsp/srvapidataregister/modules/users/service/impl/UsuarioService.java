package com.fmsp.srvapidataregister.modules.users.service.impl;

import com.fmsp.srvapidataregister.core.exceptions.InternalServerException;
import com.fmsp.srvapidataregister.modules.companies.dto.EmpresaDTO;
import com.fmsp.srvapidataregister.modules.companies.service.IEmpresaService;
import com.fmsp.srvapidataregister.modules.groups.dto.GrupoDTO;
import com.fmsp.srvapidataregister.modules.groups.service.IGrupoService;
import com.fmsp.srvapidataregister.modules.pais.service.IPaisService;
import com.fmsp.srvapidataregister.modules.roles.service.IRolService;
import com.fmsp.srvapidataregister.modules.sector.service.ISectorService;
import com.fmsp.srvapidataregister.modules.users.dto.RegistroDTO;
import com.fmsp.srvapidataregister.modules.users.dto.UsuarioDTO;
import com.fmsp.srvapidataregister.modules.users.entity.Usuario;
import com.fmsp.srvapidataregister.modules.users.repository.UsuarioRepository;
import com.fmsp.srvapidataregister.modules.users.service.IUsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    private final IRolService rolService;
    private final IEmpresaService empresaService;
    private final IGrupoService grupoService;
    private final ISectorService sectorService;
    private final IPaisService paisService;

    public UsuarioService(UsuarioRepository usuarioRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder,
                          IRolService rolService, IEmpresaService empresaService, IGrupoService grupoService,
                          ISectorService sectorService, IPaisService paisService) {
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.rolService = rolService;
        this.empresaService = empresaService;
        this.grupoService = grupoService;
        this.sectorService = sectorService;
        this.paisService = paisService;
    }

    @Override
    public Optional<UsuarioDTO> getUsuarioByUsername(String username) {
        Optional<Usuario> user = usuarioRepository.findByUsuarioWithRelations(username);
        return user.map(u -> modelMapper.map(u, UsuarioDTO.class));
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
        if(sector == null){
            throw new InternalServerException(uuid, "E001", "El sector no existe.");
        }

        var pais = paisService.findById(registroDTO.getEmpresa().getPais().getId());
        if(pais == null){
            throw new InternalServerException(uuid, "E001", "El pais no existe.");
        }

        EmpresaDTO empresaDTO = modelMapper.map(registroDTO.getEmpresa(), EmpresaDTO.class);
        empresaDTO.setPais(pais);
        empresaDTO.setSector(sector);
        empresaDTO.setEstado("ACTIVO");
        EmpresaDTO empresaGuardada = empresaService.save(empresaDTO);

        var rol = rolService.findById(1L);

        GrupoDTO grupo = new GrupoDTO();
        grupo.setNombre(registroDTO.getGrupo().getNombre());
        grupo.setEmpresa(empresaGuardada);
        GrupoDTO grupoGuardado = grupoService.save(grupo);

        UsuarioDTO usuarioDTO = modelMapper.map(registroDTO, UsuarioDTO.class);
        usuarioDTO.setUsuario(registroDTO.getUsuario());
        usuarioDTO.setRol(rol);
        usuarioDTO.setGrupo(grupoGuardado);
        usuarioDTO.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));

        var mapper = modelMapper.map(usuarioDTO, Usuario.class);
        usuarioRepository.save(mapper);
        registroDTO.setRol(rol);
        return modelMapper.map(registroDTO, UsuarioDTO.class);
    }

    public boolean getUsuarioByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}