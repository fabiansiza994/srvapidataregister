package com.fmsp.srvapidataregister.modules.pay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fmsp.srvapidataregister.modules.pay.config.MercadoPagoProperties;
import com.fmsp.srvapidataregister.modules.pay.service.dto.PreapprovalInit;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodsRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Cliente de integración con Mercado Pago (SDK/REST).
 *
 * Implementa creación de Preference (checkout) mínima. Nota: para suscripciones
 * recurrentes se puede migrar a Preapproval/PreapprovalPlan en un siguiente paso.
 */
@Service
public class MercadoPagoClient {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoClient.class);

    private final MercadoPagoProperties props;

    public MercadoPagoClient(MercadoPagoProperties props) {
        this.props = props;
    }

    /**
     * SUSCRIPCIÓN (Preapproval):
     * - Si envías preapprovalPlanId => suscripción basada en plan.
     * - Si NO envías plan => suscripción "sin plan" con auto_recurring (monto & moneda).
     *
     * Devuelve init_point para redirigir al comprador.
     */
    public PreapprovalInit createSubscriptionPreapproval(
            String preapprovalPlanId,  // puede ser null si usas auto_recurring
            String title,              // visible para el usuario
            String currency,           // "COP" por ej.
            BigDecimal amount,         // requerido si NO usas plan
            String payerEmail,         // email del comprador
            String period              // "MONTHLY" | "ANNUAL"
    ) {
        Objects.requireNonNull(payerEmail, "payerEmail is required");

        String accessToken = props.getAccessToken();
        boolean sandbox = Boolean.TRUE.equals(props.getSandbox());
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("Mercado Pago access token is required. Configure 'mp.access-token'.");
        }
        if (preapprovalPlanId == null) {
            Objects.requireNonNull(amount, "amount is required when preapprovalPlanId is null");
            Objects.requireNonNull(currency, "currency is required when preapprovalPlanId is null");
        }
        if (props.getBackUrl() == null || props.getBackUrl().isBlank()) {
            throw new IllegalStateException("mp.back-url is required for preapproval redirects.");
        }

        // Validación de entorno: si sandbox=true debemos usar un token TEST- y esperar live_mode=false
        if (sandbox) {
            log.info("Modo sandbox habilitado.");
            if (!accessToken.startsWith("TEST-")) {
                throw new IllegalStateException("Sandbox habilitado pero el access token no es de prueba (debe iniciar con 'TEST-').");
            }
        } else {
            log.info("Modo live habilitado.");
            if (accessToken.startsWith("TEST-")) {
                log.warn("[MP] Sandbox deshabilitado pero se está usando un access token de TEST. Considera configurar un token de producción.");
            }
        }

        try {
            MercadoPagoConfig.setAccessToken(accessToken);

            String externalReference = (preapprovalPlanId != null ? preapprovalPlanId : "no-plan")
                    + ":" + payerEmail + ":" + System.currentTimeMillis();

            // Mapeo del período
            int frequency;
            String frequencyType;
            switch (period == null ? "MONTHLY" : period.toUpperCase()) {
                case "ANNUAL":
                case "ANUAL":
                    frequency = 12; frequencyType = "months"; break;
                case "MONTHLY":
                default:
                    frequency = 1; frequencyType = "months"; break;
            }

            // Construir JSON payload (snake_case) según API REST de MP
            Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("payer_email", payerEmail);
            payload.put("reason", title != null ? title : "Suscripción");
            payload.put("back_url", props.getBackUrl());
            payload.put("external_reference", externalReference);
            payload.put("metadata", Map.of(
                    "source", "srvapidataregister",
                    "period", period != null ? period : "MONTHLY",
                    "type", preapprovalPlanId != null ? "PLAN" : "AUTO_RECURRING"
            ));

            if (preapprovalPlanId != null && !preapprovalPlanId.isBlank()) {
                payload.put("preapproval_plan_id", preapprovalPlanId);
            } else {
                Map<String, Object> autoRecurring = new java.util.HashMap<>();
                autoRecurring.put("frequency", frequency);
                autoRecurring.put("frequency_type", frequencyType);
                autoRecurring.put("transaction_amount", amount);
                autoRecurring.put("currency_id", currency);
                payload.put("auto_recurring", autoRecurring);
            }

            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            String body = mapper.writeValueAsString(payload);
            log.info("[MP] preapprovalRequest-json={}", body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mercadopago.com/preapproval"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("[MP] API error creando preapproval: status={} content={}", response.statusCode(), response.body());
                throw new RuntimeException("Mercado Pago API error creando preapproval: status=" + response.statusCode());
            }

            log.warn("[MP] Access token en uso (primeros 12): {}",
                    props.getAccessToken() != null ? props.getAccessToken().substring(0, Math.min(12, props.getAccessToken().length())) : "<null>");

            // Parsear respuesta
            Map<?,?> respMap = mapper.readValue(response.body(), Map.class);
            Object initPointObj = respMap.get("init_point");
            Object idObj = respMap.get("id");
            String initPoint = initPointObj != null ? initPointObj.toString() : null;
            String providerId = idObj != null ? idObj.toString() : null;

            if (initPoint == null || providerId == null) {
                log.error("[MP] Respuesta preapproval sin init_point o id: {}", response.body());
                throw new RuntimeException("Respuesta de Mercado Pago inválida al crear preapproval");
            }

            // En la API de Preapproval no siempre viene 'live_mode'. Validamos modo usando el access token.
            boolean tokenIndicaSandbox = accessToken.startsWith("TEST-");
            if (sandbox && !tokenIndicaSandbox) {
                log.error("[MP] Sandbox habilitado pero el access token no es de prueba (no inicia con TEST-)");
                throw new IllegalStateException("Sandbox habilitado pero el access token no es de prueba (no inicia con TEST-)");
            }
            if (!sandbox && tokenIndicaSandbox) {
                log.warn("[MP] Sandbox deshabilitado pero el access token parece de prueba (TEST-). Revisa configuración.");
            }

            log.info("[MP] preapproval creado id={} init_point={} currency={} amount={} period={} sandbox={} tokenSandboxIndicado={}",
                    providerId, initPoint, currency, amount, period, sandbox, tokenIndicaSandbox);

            return new PreapprovalInit(initPoint, providerId);

        } catch (Exception e) {
            log.error("[MP] Error creando preapproval", e);
            throw new RuntimeException("Error creando preapproval en Mercado Pago", e);
        }
    }

    /**
     * Compat: creación de Preference (pago único). Se mantiene por si se requiere checkout clásico.
     */
    public PreapprovalInit createPreapproval(
            String planPublicId,
            String title,
            String description,
            String currency,
            BigDecimal amount,
            String payerEmail,
            String period // "MONTHLY" | "ANNUAL"
    ) {
        Objects.requireNonNull(amount, "amount is required");
        Objects.requireNonNull(currency, "currency is required");
        Objects.requireNonNull(payerEmail, "payerEmail is required");

        String accessToken = props.getAccessToken();
        boolean sandbox = Boolean.TRUE.equals(props.getSandbox());

        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("Mercado Pago access token is required. Configure 'mp.access-token'.");
        }

        try {
            // Configurar SDK con access token
            MercadoPagoConfig.setAccessToken(accessToken);

            // Construir Item
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .id(planPublicId)
                    .title(title != null ? title : planPublicId)
                    .description(description)
                    .categoryId("services")
                    .quantity(1)
                    .currencyId(currency)
                    .unitPrice(amount)
                    .build();

            // Payer (email)
            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .email(payerEmail)
                    .build();

            // Back URLs
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(props.getSuccessUrl())
                    .pending(props.getPendingUrl())
                    .failure(props.getFailureUrl())
                    .build();

            // Métodos de pago (alineado al ejemplo: cuotas 12, sin exclusiones, método por defecto account_money)
            PreferencePaymentMethodsRequest paymentMethods = PreferencePaymentMethodsRequest.builder()
                    .excludedPaymentTypes(java.util.Collections.emptyList())
                    .excludedPaymentMethods(java.util.Collections.emptyList())
                    .installments(12)
                    .defaultPaymentMethodId("account_money")
                    .build();

            String externalReference = planPublicId + ":" + payerEmail + ":" + System.currentTimeMillis();

            // Preference request
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(java.util.List.of(item))
                    .payer(payer)
                    .notificationUrl(props.getWebhookUrl())
                    .backUrls(backUrls)
                    .autoReturn("all")
                    .statementDescriptor("TestStore")
                    .binaryMode(false)
                    .externalReference(externalReference)
                    .paymentMethods(paymentMethods)
                    // Metadata útil
                    .metadata(java.util.Map.of(
                            "planPublicId", planPublicId,
                            "period", period,
                            "source", "srvapidataregister") )
                    .build();

            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            log.info("[MP] preferenceRequest={}", mapper.writeValueAsString(preferenceRequest));

            Preference preference = new com.mercadopago.client.preference.PreferenceClient().create(preferenceRequest);

            String initPoint = sandbox ? preference.getSandboxInitPoint() : preference.getInitPoint();
            String providerId = preference.getId();
            log.info("[MP] preference creada id={} init_point={} currency={} amount={} period={} sandbox={}", providerId, initPoint, currency, amount, period, sandbox);
            return new PreapprovalInit(initPoint, providerId);
        } catch (MPApiException e) {
            String msg = String.format("Mercado Pago API error creando preference: status=%d", e.getStatusCode());
            log.error("[MP] {} content={}", msg, e.getApiResponse() != null ? e.getApiResponse().getContent() : "<no content>", e);
            throw new RuntimeException(msg, e);
        } catch (MPException e) {
            log.error("[MP] SDK error creando preference", e);
            throw new RuntimeException("Mercado Pago SDK error creando preference", e);
        } catch (Exception e) {
            log.error("[MP] Error inesperado creando preference", e);
            throw new RuntimeException("Error inesperado creando preference en Mercado Pago", e);
        }
    }
}
