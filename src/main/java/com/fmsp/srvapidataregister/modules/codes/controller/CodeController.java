package com.fmsp.srvapidataregister.modules.codes.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.codes.service.ICodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("code")
public class CodeController {
    private final ICodeService codeService;

    public CodeController(ICodeService codeService) {
        this.codeService = codeService;
    }

    @GetMapping("/{userId}/{code}")
    public ResponseEntity<ApiResponse<Object>> verifyCode(@PathVariable("userId") Long  userId, @PathVariable("code") String code) {
        var response = codeService.verifyCode(userId, code);
        return ResponseHandler.successResponse(response, "123");
    }
}
