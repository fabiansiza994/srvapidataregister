package com.fmsp.srvapidataregister.modules.codes.service.impl;

import com.fmsp.srvapidataregister.modules.codes.CodeRepository;
import com.fmsp.srvapidataregister.modules.codes.entity.Code;
import com.fmsp.srvapidataregister.modules.codes.entity.dto.CodeDTO;
import com.fmsp.srvapidataregister.modules.codes.service.ICodeService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class CodeServiceService implements ICodeService {

    private final CodeRepository codeRepository;
    private final ModelMapper modelMapper;

    public CodeServiceService(CodeRepository codeRepository, ModelMapper modelMapper) {
        this.codeRepository = codeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CodeDTO save(CodeDTO codeDTO) {
        var codeSaved = codeRepository.save(modelMapper.map(codeDTO, Code.class));
        return modelMapper.map(codeSaved, CodeDTO.class);
    }
}
