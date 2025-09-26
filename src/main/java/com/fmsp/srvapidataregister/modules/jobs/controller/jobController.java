package com.fmsp.srvapidataregister.modules.jobs.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ErrorItemDTO;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoCreateDTO;
import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoDTO;
import com.fmsp.srvapidataregister.modules.jobs.service.IJobService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("job")
public class jobController {

    private final IJobService jobService;

    public jobController(IJobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> create(
            @RequestPart("payload") @Valid TrabajoCreateDTO payload,
            BindingResult result,
            @RequestPart(value = "foto1", required = false) MultipartFile foto1,
            @RequestPart(value = "foto2", required = false) MultipartFile foto2,
            @RequestPart(value = "foto3", required = false) MultipartFile foto3,
            @RequestPart(value = "foto4", required = false) MultipartFile foto4
    ) {

        String idTx = UUID.randomUUID().toString();

        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO("E400", error.getDefaultMessage(), error.getField()))
                    .collect(Collectors.toList());
            return ResponseHandler.badRequestResponse(errores, idTx);
        }
        TrabajoDTO created = jobService.create(payload, foto1, foto2, foto3, foto4, idTx);
        return ResponseHandler.successResponse(created, idTx);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        String idTx = UUID.randomUUID().toString();
        var pageResult = jobService.searchTrabajos(q, page, size, sortBy, direction);

        var payload = new HashMap<>();
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

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<Object>> detail(@PathVariable("id") Long id) {
        String idTx = UUID.randomUUID().toString();
        var detail = jobService.getTrabajoDetail(id, idTx);
        return ResponseHandler.successResponse(detail, idTx);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") Long id) {
        String idTx = UUID.randomUUID().toString();
        jobService.deleteTrabajo(id, idTx);

        var payload = new HashMap<String, Object>();
        payload.put("deletedId", id);
        payload.put("message", "Trabajo eliminado correctamente");

        return ResponseHandler.successResponse(payload, idTx);
    }
}
