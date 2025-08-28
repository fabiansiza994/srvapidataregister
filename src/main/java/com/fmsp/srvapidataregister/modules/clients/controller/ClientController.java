package com.fmsp.srvapidataregister.modules.clients.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ErrorItemDTO;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.clients.dto.ClienteDTO;
import com.fmsp.srvapidataregister.modules.clients.service.IClienteService;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("client")
public class ClientController {

    private final IClienteService clienteService;

    public ClientController(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody @Valid ClienteDTO clienteDTO, BindingResult result) {
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
        var client = clienteService.createClient(clienteDTO, uuid);
        return ResponseHandler.successResponse(client, uuid);
    }
}
