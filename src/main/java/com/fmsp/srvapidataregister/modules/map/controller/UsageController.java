package com.fmsp.srvapidataregister.modules.map.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.map.entity.UsagePing;
import com.fmsp.srvapidataregister.modules.map.service.impl.UsageService;
import com.fmsp.srvapidataregister.modules.map.sto.UsagePingReq;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("usage")
public class UsageController {

    private final UsageService service;

    public UsageController(UsageService service) {
        this.service = service;
    }

    @PostMapping("/ping")
    public ResponseEntity<ApiResponse<Object>> ping(@RequestBody UsagePingReq req, HttpServletRequest http) {
        String idTx = UUID.randomUUID().toString();
        UsagePing ping = new UsagePing();
        ping.setLat(req.lat());
        ping.setLng(req.lng());
        ping.setUserId(req.userId());
        ping.setAppVersion(req.appVersion());
        ping.setTz(req.tz());
        ping.setPlatform(req.platform());

        service.ping(ping, http);
        return ResponseHandler.successResponse(new java.util.HashMap<>(), idTx);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Object>> list() {
        String idTx = UUID.randomUUID().toString();
        var points = service.list();
        return ResponseHandler.successResponse(points, idTx);
    }
}
