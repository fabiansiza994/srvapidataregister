package com.fmsp.srvapidataregister.modules.sector.sectorController;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.sector.service.ISectorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("sector")
public class SectorController {

    private final ISectorService sectorService;

    public SectorController(ISectorService sectorService) {
        this.sectorService = sectorService;
    }

    @GetMapping("list")
    public ResponseEntity<ApiResponse<Object>> list() {
        String uuid = UUID.randomUUID().toString();
        var pais = sectorService.findAllActive();
        return ResponseHandler.successResponse(pais, uuid);
    }
}
