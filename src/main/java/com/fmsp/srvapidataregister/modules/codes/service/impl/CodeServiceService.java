package com.fmsp.srvapidataregister.modules.codes.service.impl;

import com.fmsp.srvapidataregister.core.exceptions.CustomServiceException;
import com.fmsp.srvapidataregister.modules.codes.CodeRepository;
import com.fmsp.srvapidataregister.modules.codes.entity.Code;
import com.fmsp.srvapidataregister.modules.codes.entity.dto.CodeDTO;
import com.fmsp.srvapidataregister.modules.codes.service.ICodeService;
import com.fmsp.srvapidataregister.modules.users.service.IUsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class CodeServiceService implements ICodeService {

    private final IUsuarioService usuarioService;
    private final CodeRepository codeRepository;
    private final ModelMapper modelMapper;

    public CodeServiceService(IUsuarioService usuarioService, CodeRepository codeRepository, ModelMapper modelMapper) {
        this.usuarioService = usuarioService;
        this.codeRepository = codeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CodeDTO save(CodeDTO codeDTO) {
        var codeSaved = codeRepository.save(modelMapper.map(codeDTO, Code.class));
        return modelMapper.map(codeSaved, CodeDTO.class);
    }

    @Override
    public boolean existCodeAndUserEmail(String userEmail) {
        var codeDB = codeRepository.findByUserEmail(userEmail);
       return codeDB.stream()
                .anyMatch(c -> "ACTIVO".equalsIgnoreCase(c.getStatus()));
    }

    @Override
    public CodeDTO findByCodeAndUserEmail(String code, String userEmail) {
        var codeDB = codeRepository.findByCodeAndUserEmail(code, userEmail);
        return codeDB.stream()
                .filter(c -> "ACTIVO".equalsIgnoreCase(c.getStatus()))
                .findFirst()
                .map(c -> modelMapper.map(c, CodeDTO.class))
                .orElse(null);
    }

    @Override
    public CodeDTO verifyCode(Long userId, String code) {
        // traer el usuario
        var user = usuarioService.getUsuarioById(userId);
        if(user.isEmpty()){
            throw new CustomServiceException("123", "E004", "usuario no encontrado");
        }

        // validar el codigo
        var codeDB = findByCodeAndUserEmail(code, user.get().getEmail());
        if(codeDB == null){
            throw new CustomServiceException("123", "E004", "codigo inexistente");
        }

        // activar usuario
        user.get().setBloqueado(false);
        usuarioService.saveUsuario(user.get());

        // desactivar codigo
        codeDB.setStatus("INACTIVE");
        codeRepository.save(modelMapper.map(codeDB, Code.class));
        return codeDB;
    }
}
