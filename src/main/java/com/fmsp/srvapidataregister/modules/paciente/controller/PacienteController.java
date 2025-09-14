package com.fmsp.srvapidataregister.modules.paciente.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.paciente.service.IPacienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("paciente")
public class PacienteController {

    private final IPacienteService pacienteService;

    public PacienteController(IPacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping("list/{clientId}")
    public ResponseEntity<ApiResponse<Object>> list(@PathVariable Integer clientId) {
        String uuid = UUID.randomUUID().toString();
        var client = pacienteService.listPatient(Long.valueOf(clientId));
        return ResponseHandler.successResponse(client, uuid);
    }
}
