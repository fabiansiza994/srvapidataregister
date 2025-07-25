package com.fmsp.srvapidataregister.modules.auth.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class DemoController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Object>> adminEndpoint() {
        UUID uuid = UUID.randomUUID();
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseHandler.successResponse("👑 Hola "+user.getUsername()+", bienvenido al dashboard.", uuid.toString());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/procesos")
    public ResponseEntity<ApiResponse<Object>> userEndpoint() {
        UUID uuid = UUID.randomUUID();
        return ResponseHandler.successResponse("🛠️ Hola , puedes ver y gestionar procesos.", uuid.toString());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @GetMapping("/estado")
    public ResponseEntity<ApiResponse<Object>> clientEndpoint() {
        UUID uuid = UUID.randomUUID();
        return ResponseHandler.successResponse("📄 Hola CLIENT/ADMIN, puedes ver los estados de los procesos.", uuid.toString());
    }
}