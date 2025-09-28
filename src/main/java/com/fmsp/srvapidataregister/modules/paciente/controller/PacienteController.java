package com.fmsp.srvapidataregister.modules.paciente.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ErrorItemDTO;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteUpdateDTO;
import com.fmsp.srvapidataregister.modules.paciente.service.IPacienteService;
import com.fmsp.srvapidataregister.modules.paciente.service.impl.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
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

    // Recomendado: listar por clientId con paginación
    @GetMapping("/list-by-client")
    public ResponseEntity<ApiResponse<Object>> listByClient(
            @RequestParam Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        String uuid = UUID.randomUUID().toString();
        var pageResult = ((PacienteService) pacienteService)
                .listarPacientesPorCliente(clientId, page, size, sortBy, direction);

        var payload = new HashMap<>();
        payload.put("items", pageResult.getContent());
        payload.put("page", pageResult.getNumber());
        payload.put("size", pageResult.getSize());
        payload.put("totalElements", pageResult.getTotalElements());
        payload.put("totalPages", pageResult.getTotalPages());
        payload.put("last", pageResult.isLast());
        payload.put("sort", pageResult.getSort().toString());
        payload.put("clientId", clientId);

        return ResponseHandler.successResponse(payload, uuid);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) throws AccessDeniedException {
        String uuid = UUID.randomUUID().toString();
        var pageResult = ((PacienteService) pacienteService)
                .searchPacientes(q, page, size, sortBy, direction);

        var payload = new HashMap<String, Object>();
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
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") Long id) throws AccessDeniedException {
        String uuid = UUID.randomUUID().toString();
        ((PacienteService) pacienteService).deletePaciente(id, uuid);

        var payload = new HashMap<String, Object>();
        payload.put("deletedId", id);
        payload.put("message", "Paciente eliminado correctamente");

        return ResponseHandler.successResponse(payload, uuid);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<Object>> getDetail(@PathVariable("id") Long id) throws AccessDeniedException {
        String uuid = UUID.randomUUID().toString();
        var detail = ((PacienteService) pacienteService).getPacienteDetail(id, uuid);
        return ResponseHandler.successResponse(detail, uuid);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Object>> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid PacienteUpdateDTO dto,
            BindingResult result
    ) throws AccessDeniedException {
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

        var updated = ((PacienteService) pacienteService).updatePaciente(id, dto, uuid);
        return ResponseHandler.successResponse(updated, uuid);
    }
}
