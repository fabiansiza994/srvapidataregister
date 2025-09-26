package com.fmsp.srvapidataregister.modules.groups.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ErrorItemDTO;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.groups.dto.GrupoCreateDTO;
import com.fmsp.srvapidataregister.modules.groups.dto.GrupoUpdateDTO;
import com.fmsp.srvapidataregister.modules.groups.service.IGrupoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("group")
public class GroupController {

    private final IGrupoService grupoService;

    public GroupController(IGrupoService grupoService) {
        this.grupoService = grupoService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> create(@RequestBody @Valid GrupoCreateDTO dto,
                                                      BindingResult result) {
        String idTx = UUID.randomUUID().toString();
        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO("E400", error.getDefaultMessage(), error.getField()))
                    .collect(Collectors.toList());
            return ResponseHandler.badRequestResponse(errores, idTx);
        }
        var created = grupoService.create(dto, idTx);
        return ResponseHandler.successResponse(created, idTx);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Object>> update(@PathVariable Long id,
                                                      @RequestBody @Valid GrupoUpdateDTO dto,
                                                      BindingResult result) {
        String idTx = UUID.randomUUID().toString();
        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO("E400", error.getDefaultMessage(), error.getField()))
                    .collect(Collectors.toList());
            return ResponseHandler.badRequestResponse(errores, idTx);
        }
        var updated = grupoService.update(id, dto, idTx);
        return ResponseHandler.successResponse(updated, idTx);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) {
        String idTx = UUID.randomUUID().toString();
        grupoService.delete(id, idTx);

        var payload = new HashMap<>();
        payload.put("deletedId", id);
        payload.put("message", "Grupo eliminado correctamente");
        return ResponseHandler.successResponse(payload, idTx);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<Object>> detail(@PathVariable Long id) {
        String idTx = UUID.randomUUID().toString();
        var detail = grupoService.findDetail(id, idTx)
                .orElseThrow(() -> new NoSuchElementException("Grupo no encontrado"));
        return ResponseHandler.successResponse(detail, idTx);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Object>> list(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "id") String sortBy,
                                                    @RequestParam(defaultValue = "DESC") String direction) {
        String idTx = UUID.randomUUID().toString();
        var pageResult = grupoService.list(page, size, sortBy, direction);

        var payload = new HashMap<String, Object>();
        payload.put("items", pageResult.getContent());
        payload.put("page", pageResult.getNumber());
        payload.put("size", pageResult.getSize());
        payload.put("totalElements", pageResult.getTotalElements());
        payload.put("totalPages", pageResult.getTotalPages());
        payload.put("last", pageResult.isLast());
        payload.put("sort", pageResult.getSort().toString());

        return ResponseHandler.successResponse(payload, idTx);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> search(@RequestParam String q,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "id") String sortBy,
                                                      @RequestParam(defaultValue = "DESC") String direction) {
        String idTx = UUID.randomUUID().toString();
        var pageResult = grupoService.search(q, page, size, sortBy, direction);

        var payload = new HashMap<String, Object>();
        payload.put("items", pageResult.getContent());
        payload.put("page", pageResult.getNumber());
        payload.put("size", pageResult.getSize());
        payload.put("totalElements", pageResult.getTotalElements());
        payload.put("totalPages", pageResult.getTotalPages());
        payload.put("last", pageResult.isLast());
        payload.put("sort", pageResult.getSort().toString());
        payload.put("query", q);

        return ResponseHandler.successResponse(payload, idTx);
    }
}
