package com.fmsp.srvapidataregister.modules.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailAttachmentRequest(
        @NotBlank @Email String to,
        @NotBlank String subject,
        @NotBlank String htmlBody,
        // contenido del adjunto en Base64 (útil cuando llamas desde Angular)
        @NotBlank String filename,
        @NotBlank String base64Content,
        @NotBlank String contentType // ej: "application/pdf" o "image/png"
) {}