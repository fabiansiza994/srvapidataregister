package com.fmsp.srvapidataregister.modules.codes.service;

import com.fmsp.srvapidataregister.modules.codes.entity.dto.CodeDTO;

public interface ICodeService {
    CodeDTO save(CodeDTO codeDTO);
}
