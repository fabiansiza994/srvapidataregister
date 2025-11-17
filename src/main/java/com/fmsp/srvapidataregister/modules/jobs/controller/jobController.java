package com.fmsp.srvapidataregister.modules.jobs.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ErrorItemDTO;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoCreateDTO;
import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoDTO;
import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoUpdateDTO;
import com.fmsp.srvapidataregister.modules.jobs.service.IJobService;
import com.fmsp.srvapidataregister.modules.jobs.service.impl.JobReportService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    private final JobReportService jobReportService;
    private final IJobService jobService;

    public jobController(JobReportService jobReportService, IJobService jobService) {
        this.jobReportService = jobReportService;
        this.jobService = jobService;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> create(
            @RequestPart("payload") @Valid TrabajoCreateDTO payload,
            BindingResult result,
            @RequestPart(value = "foto1", required = false) MultipartFile foto1,
            @RequestPart(value = "foto2", required = false) MultipartFile foto2,
            @RequestPart(value = "foto3", required = false) MultipartFile foto3,
            @RequestPart(value = "foto4", required = false) MultipartFile foto4,
            @RequestPart(value = "foto5", required = false) MultipartFile foto5,
            @RequestPart(value = "foto6", required = false) MultipartFile foto6
    ) {

        String idTx = UUID.randomUUID().toString();

        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO("E400", error.getDefaultMessage(), error.getField()))
                    .collect(Collectors.toList());
            return ResponseHandler.badRequestResponse(errores, idTx);
        }
        TrabajoDTO created = jobService.create(payload, foto1, foto2, foto3, foto4, foto5, foto6, idTx);
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
        jobService.delete(id, idTx);

        var payload = new HashMap<String, Object>();
        payload.put("deletedId", id);
        payload.put("message", "Trabajo eliminado correctamente");

        return ResponseHandler.successResponse(payload, idTx);
    }

    // modules/jobs/controller/jobController.java
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> update(
            @PathVariable("id") Long id,
            @RequestPart("payload") @Valid TrabajoUpdateDTO payload,
            BindingResult result,
            @RequestPart(value = "foto1", required = false) MultipartFile foto1,
            @RequestPart(value = "foto2", required = false) MultipartFile foto2,
            @RequestPart(value = "foto3", required = false) MultipartFile foto3,
            @RequestPart(value = "foto4", required = false) MultipartFile foto4,
            @RequestPart(value = "foto5", required = false) MultipartFile foto5,
            @RequestPart(value = "foto6", required = false) MultipartFile foto6
    ) {
        String idTx = UUID.randomUUID().toString();

        if (!id.equals(payload.getId())) {
            var errores = List.of(new ErrorItemDTO("E400", "El id del path no coincide con el del payload", "id"));
            return ResponseHandler.badRequestResponse(errores, idTx);
        }

        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(e -> new ErrorItemDTO("E400", e.getDefaultMessage(), e.getField()))
                    .collect(Collectors.toList());
            return ResponseHandler.badRequestResponse(errores, idTx);
        }

        TrabajoDTO updated = jobService.update(
                payload, foto1, foto2, foto3, foto4, foto5, foto6, idTx
        );
        return ResponseHandler.successResponse(updated, idTx);
    }

    @GetMapping("/report/excel")
    public ResponseEntity<byte[]> reportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate to,
            @RequestParam Long empresaId
    ) {
        String idTx = java.util.UUID.randomUUID().toString();
        // (opcional) validar rango
        if (from.isAfter(to)) {
            var errores = java.util.List.of(new ErrorItemDTO("E400", "from > to", "range"));
            return null;
        }

        byte[] xlsx = jobReportService.buildExcel(from, to, empresaId);

        String filename = String.format("reporte_trabajos_%s_a_%s.xlsx", from, to);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.setContentLength(xlsx.length);

        // Si quieres envolver en ApiResponse, no uses content-disposition; para descarga es mejor bytes directos.
        return new ResponseEntity<>(xlsx, headers, HttpStatus.OK);
    }
}
