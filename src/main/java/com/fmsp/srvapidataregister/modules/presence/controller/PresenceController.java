package com.fmsp.srvapidataregister.modules.presence.controller;

import com.fmsp.srvapidataregister.modules.presence.service.PresenceService;
import com.fmsp.srvapidataregister.modules.auth.service.PermisoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("presence")
public class PresenceController {
    private final PresenceService presence;
    private final PermisoService permisoService;

    public PresenceController(PresenceService presence, PermisoService permisoService) {
        this.presence = presence;
        this.permisoService = permisoService;
    }

    @PostMapping("/ping")
    public ResponseEntity<?> ping(Authentication auth) {
        // Saca datos del JWT/Authentication
        var principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = principal.getUsername();
        // Si tienes un UserDetails propio, extrae id/nombre/apellido del token/DB:
        Long userId = permisoService.getUsuarioActual().get().getId(); // implementa tú
        String nombre = ""; String apellido = "";
        presence.ping(userId, username, nombre, apellido);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @GetMapping("/online")
    public ResponseEntity<?> online() {
        // Responde lista de usuarios online (ya filtrados por TTL)
        var raw = presence.listRawOnline();
        return ResponseEntity.ok(Map.of(
                "dataResponse", Map.of("response","SUCCESS"),
                "data", raw // es JSON string; si prefieres, parsea a objetos y retorna lista tipada
        ));
    }

}
