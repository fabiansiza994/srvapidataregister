package com.fmsp.srvapidataregister.modules.clients.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ErrorItemDTO;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.clients.dto.ClienteDTO;
import com.fmsp.srvapidataregister.modules.clients.dto.ClienteUpdateDTO;
import com.fmsp.srvapidataregister.modules.clients.service.IClienteService;
import com.fmsp.srvapidataregister.modules.clients.service.impl.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<Object>> create(@RequestBody @Valid ClienteDTO clienteDTO, BindingResult result) {
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

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        String uuid = UUID.randomUUID().toString();
        var pageResult = ((ClienteService) clienteService)
                .listarClientes(page, size, sortBy, direction);

        var payload = new java.util.HashMap<String, Object>();
        payload.put("items", pageResult.getContent());
        payload.put("page", pageResult.getNumber());
        payload.put("size", pageResult.getSize());
        payload.put("totalElements", pageResult.getTotalElements());
        payload.put("totalPages", pageResult.getTotalPages());
        payload.put("last", pageResult.isLast());
        payload.put("sort", pageResult.getSort().toString());

        return ResponseHandler.successResponse(payload, uuid);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        String uuid = UUID.randomUUID().toString();

        var pageResult = ((ClienteService) clienteService)
                .searchClientes(q, page, size, sortBy, direction);

        var payload = new java.util.HashMap<String, Object>();
        payload.put("items", pageResult.getContent());
        payload.put("page", pageResult.getNumber());
        payload.put("size", pageResult.getSize());
        payload.put("totalElements", pageResult.getTotalElements());
        payload.put("totalPages", pageResult.getTotalPages());
        payload.put("last", pageResult.isLast());
        payload.put("sort", pageResult.getSort().toString());
        payload.put("query", q);

        return ResponseHandler.successResponse(payload, uuid);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") Long id) {
        String uuid = UUID.randomUUID().toString();
        ((ClienteService) clienteService).deleteCliente(id, uuid);

        var payload = new java.util.HashMap<String, Object>();
        payload.put("deletedId", id);
        payload.put("message", "Cliente eliminado correctamente");

        return ResponseHandler.successResponse(payload, uuid);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<Object>> getDetail(@PathVariable("id") Long id) {
        String uuid = UUID.randomUUID().toString();
        var detail = ((ClienteService) clienteService).getClienteDetail(id, uuid);
        return ResponseHandler.successResponse(detail, uuid);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Object>> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid ClienteUpdateDTO dto,
            BindingResult result
    ) {
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

        var updated = ((ClienteService) clienteService).updateCliente(id, dto, uuid);
        return ResponseHandler.successResponse(updated, uuid);
    }

}