package com.fmsp.srvapidataregister.modules.paciente.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ErrorItemDTO;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.clients.dto.ClienteDTO;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import com.fmsp.srvapidataregister.modules.paciente.service.IPacienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> create(@RequestBody @Valid PacienteDTO pacienteDTO, BindingResult result) {
        String uuid = UUID.randomUUID().toString();
        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO(
                            "E400",
                            error.getDefaultMessage(),
                            error.getField()))
                    .collect(Collectors.toList());

            return ResponseHandler.badRequestResponse(errores, uuid);
        }
        var client = pacienteService.save(pacienteDTO);
        return ResponseHandler.successResponse(client, uuid);
    }
}
