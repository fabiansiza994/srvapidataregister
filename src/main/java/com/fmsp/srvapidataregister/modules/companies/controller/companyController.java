package com.fmsp.srvapidataregister.modules.companies.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ErrorItemDTO;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.companies.service.ICompanyService;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("company")
public class companyController {

    private final ICompanyService companyService;

    public companyController(ICompanyService companyService) {
        this.companyService = companyService;
    }


    @GetMapping("/{empresaId}/settings")
    public ResponseEntity<ApiResponse<Object>> getSettings(@PathVariable("empresaId") Long empresaId) {
        String uuid = UUID.randomUUID().toString();
        var dto = companyService.getSettings(empresaId, uuid);
        return ResponseHandler.successResponse(dto, uuid);
    }

    @PutMapping("/allowView/{empresaId}")
    public ResponseEntity<ApiResponse<Object>> allowView(@PathVariable("empresaId") Long empresaId,
                                                         @RequestParam("enabled") boolean enabled) {
        String uuid = UUID.randomUUID().toString();
        var dto = companyService.setAllowView(empresaId, enabled, uuid);
        String msg = enabled ? "Ver trabajos entre grupos habilitado." : "Ver trabajos entre grupos deshabilitado.";
        return ResponseHandler.successResponse(dto, uuid);
    }

    @PutMapping("/allowEdit/{empresaId}")
    public ResponseEntity<ApiResponse<Object>> allowEdit(@PathVariable("empresaId") Long empresaId,
                                                         @RequestParam("enabled") boolean enabled) {
        String uuid = UUID.randomUUID().toString();
        var dto = companyService.setAllowEdit(empresaId, enabled, uuid);
        String msg = enabled ? "Editar trabajos entre grupos habilitado." : "Editar trabajos entre grupos deshabilitado.";
        return ResponseHandler.successResponse(dto, uuid);
    }

    @PutMapping("/{empresaId}/settings")
    public ResponseEntity<ApiResponse<Object>> updateSettings(@PathVariable("empresaId") Long empresaId,
                                                              @RequestBody @NotNull UpdateSettingsRequest req) {
        String uuid = UUID.randomUUID().toString();

        // validación simple (si necesitas errores como en tu create)
        List<ErrorItemDTO> errores = new ArrayList<>();
        if (req.getAllowEdit() && !req.getAllowView()) {
            errores.add(new ErrorItemDTO("E400",
                    "Para permitir editar entre grupos, también debe permitirse ver entre grupos.",
                    "allowEdit"));
            return ResponseHandler.badRequestResponse(errores, uuid);
        }

        var dto = companyService.updateSettings(empresaId, req.getAllowView(), req.getAllowEdit(), uuid);
        return ResponseHandler.successResponse(dto, uuid);
    }

    @Data
    public static class UpdateSettingsRequest {
        private Boolean allowView = false;
        private Boolean allowEdit = false;
    }
}
