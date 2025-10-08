package com.fmsp.srvapidataregister.modules.codes.service;

import com.fmsp.srvapidataregister.modules.codes.entity.dto.CodeDTO;

public interface ICodeService {
    CodeDTO save(CodeDTO codeDTO);
    boolean existCodeAndUserEmail(String userEmail);
    CodeDTO findByCodeAndUserEmail(String code, String userEmail);
    CodeDTO verifyCode(Long userId, String code);
}
