package com.fmsp.srvapidataregister.modules.pais;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.pais.service.IPaisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("country")
public class paisController {

    private final IPaisService paisService;

    public paisController(IPaisService paisService) {
        this.paisService = paisService;
    }

    @GetMapping("list")
    public ResponseEntity<ApiResponse<Object>> list() {
        String uuid = UUID.randomUUID().toString();
        var pais = paisService.findAllActive();
        return ResponseHandler.successResponse(pais, uuid);
    }
}