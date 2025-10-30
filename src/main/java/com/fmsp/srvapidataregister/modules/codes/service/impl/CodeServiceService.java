package com.fmsp.srvapidataregister.modules.codes.service.impl;

import com.fmsp.srvapidataregister.config.LoadDataConfig;
import com.fmsp.srvapidataregister.core.exceptions.CustomServiceException;
import com.fmsp.srvapidataregister.core.util.GenerateVerificationCode;
import com.fmsp.srvapidataregister.modules.codes.CodeRepository;
import com.fmsp.srvapidataregister.modules.codes.entity.Code;
import com.fmsp.srvapidataregister.modules.codes.entity.dto.CodeDTO;
import com.fmsp.srvapidataregister.modules.codes.service.ICodeService;
import com.fmsp.srvapidataregister.modules.notification.dto.EmailTemplateRequest;
import com.fmsp.srvapidataregister.modules.notification.service.EmailService;
import com.fmsp.srvapidataregister.modules.users.service.IUsuarioService;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CodeServiceService implements ICodeService {

    private final IUsuarioService usuarioService;
    private final CodeRepository codeRepository;
    private final GenerateVerificationCode generateVerificationCode;
    private final EmailService emailService;
    private final LoadDataConfig loadDataConfig;
    private final ModelMapper modelMapper;

    public CodeServiceService(IUsuarioService usuarioService, CodeRepository codeRepository, GenerateVerificationCode generateVerificationCode, EmailService emailService, LoadDataConfig loadDataConfig, ModelMapper modelMapper) {
        this.usuarioService = usuarioService;
        this.codeRepository = codeRepository;
        this.generateVerificationCode = generateVerificationCode;
        this.emailService = emailService;
        this.loadDataConfig = loadDataConfig;
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
        if (user.isEmpty()) {
            throw new CustomServiceException("123", "E004", "usuario no encontrado");
        }

        // validar el codigo
        var codeDB = findByCodeAndUserEmail(code, user.get().getEmail());
        if (codeDB == null) {
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

    @Override
    public CodeDTO resendCode(Long userId) throws MessagingException {

        var user = usuarioService.getUsuarioById(userId);
        if (user.isEmpty()) {
            throw new CustomServiceException("123", "E004", "usuario no encontrado");
        }

        if (!user.get().isBloqueado()) {
            throw new CustomServiceException("123", "E004", "usuario ya se encuentra activo");
        }

        List<Code> codeDB = codeRepository.findByUserEmail(user.get().getEmail());

        Optional<Code> activeCode = codeDB.stream()
                .filter(c -> "ACTIVO".equalsIgnoreCase(c.getStatus()))
                .findFirst();


        if (activeCode.isPresent()) {
            activeCode.get().setStatus("INACTIVO");
            codeRepository.save(modelMapper.map(codeDB, Code.class));
        }

        String newCode = generateVerificationCode.generarCodigoVerificacion();
        Map<String, Object> variables = new HashMap();
        variables.put("nombre", user.get().getNombre());
        variables.put("mensaje", "Estas a un paso!.");
        variables.put("mensageTwo", "tu codigo de activacion es: <b>" + newCode + "</b>");
        variables.put("ctaUrl", loadDataConfig.getFrontUrl() + user.get().getId());

        emailService.sendTemplate(new EmailTemplateRequest(user.get().getEmail(), "Bienvenido a DataRegister",
                "hello",
                variables));
        CodeDTO codeDTO = new CodeDTO();
        codeDTO.setCode(newCode);
        codeDTO.setUserEmail(user.get().getEmail());
        codeDTO.setStatus("ACTIVO");
        var codeResult = codeRepository.save(modelMapper.map(codeDTO, Code.class));

        return modelMapper.map(codeResult, CodeDTO.class);
    }
}