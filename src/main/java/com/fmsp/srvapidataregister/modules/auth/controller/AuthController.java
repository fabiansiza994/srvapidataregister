package com.fmsp.srvapidataregister.modules.auth.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.auth.dto.AuthResponse;
import com.fmsp.srvapidataregister.modules.auth.dto.LoginRequest;
import com.fmsp.srvapidataregister.modules.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody LoginRequest request) {
        UUID uuid = UUID.randomUUID();
        AuthResponse authResponse = authService.login(request);
        return ResponseHandler.successResponse(authResponse, uuid.toString());
    }

}