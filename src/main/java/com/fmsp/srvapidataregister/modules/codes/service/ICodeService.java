package com.fmsp.srvapidataregister.modules.codes.service;

import com.fmsp.srvapidataregister.modules.codes.entity.dto.CodeDTO;
import jakarta.mail.MessagingException;

public interface ICodeService {
    CodeDTO save(CodeDTO codeDTO);
    boolean existCodeAndUserEmail(String userEmail);
    CodeDTO findByCodeAndUserEmail(String code, String userEmail);
    CodeDTO verifyCode(Long userId, String code);
    CodeDTO resendCode(Long userId) throws MessagingException;
}
