package com.fmsp.srvapidataregister.modules.pay.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("pay")
public class PayWebhookController {

    private static final Logger log = LoggerFactory.getLogger(PayWebhookController.class);

    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<Object>> webhook(@RequestBody(required = false) Map<String, Object> body,
                                                       @RequestHeader Map<String, String> headers,
                                                       @RequestParam(required = false) Map<String, String> params) {
        String idTx = UUID.randomUUID().toString();
        try {
            log.info("[MP][WEBHOOK] idTx={} headers={} params={} body={} ", idTx, headers, params, body);
            // TODO: validar firma, verificar tipo de notificación y encolar procesamiento idempotente
            return ResponseHandler.successResponse(Map.of("status", "received"), idTx);
        } catch (Exception ex) {
            log.error("[MP][WEBHOOK] error idTx=" + idTx, ex);
            return ResponseHandler.internalServerResponse(java.util.List.of(), idTx);
        }
    }
}
