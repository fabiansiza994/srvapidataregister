package com.fmsp.srvapidataregister.modules.clients.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.auth.dto.AuthResponse;
import com.fmsp.srvapidataregister.modules.auth.dto.LoginRequest;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("clients")
public class ClientController {

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody PacienteDTO pacienteDTO) {
        UUID uuid = UUID.randomUUID();

        return ResponseHandler.successResponse(null, uuid.toString());
    }
}
