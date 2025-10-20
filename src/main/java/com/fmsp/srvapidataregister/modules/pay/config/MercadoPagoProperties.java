package com.fmsp.srvapidataregister.modules.pay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mp")
public class MercadoPagoProperties {
    /** Access token de Mercado Pago (usar sandbox para pruebas). */
    private String accessToken;
    /** Public key para front-end si aplica. */
    private String publicKey;
    /** Moneda por defecto (ej. ARS, MXN, COP, CLP, PEN). */
    private String currency = "USD"; // TODO: confirmar con el usuario
    /** Habilitar sandbox (true/false). */
    private Boolean sandbox = true;

    /** URL de retorno principal para preapproval (redirect). */
    private String backUrl;

    /** URLs de retorno del checkout/suscripción */
    private String successUrl;
    private String pendingUrl;
    private String failureUrl;

    /** Webhook/notification URL para IPN de Mercado Pago */
    private String webhookUrl;

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Boolean getSandbox() { return sandbox; }
    public void setSandbox(Boolean sandbox) { this.sandbox = sandbox; }

    public String getBackUrl() { return backUrl; }
    public void setBackUrl(String backUrl) { this.backUrl = backUrl; }

    public String getSuccessUrl() { return successUrl; }
    public void setSuccessUrl(String successUrl) { this.successUrl = successUrl; }

    public String getPendingUrl() { return pendingUrl; }
    public void setPendingUrl(String pendingUrl) { this.pendingUrl = pendingUrl; }

    public String getFailureUrl() { return failureUrl; }
    public void setFailureUrl(String failureUrl) { this.failureUrl = failureUrl; }

    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
}