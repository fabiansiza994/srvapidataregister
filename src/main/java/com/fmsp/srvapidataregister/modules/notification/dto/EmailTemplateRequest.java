package com.fmsp.srvapidataregister.modules.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record EmailTemplateRequest (
        @NotBlank @Email String to,
        @NotBlank String subject,
        @NotBlank String template,           // nombre del template, ej: "hello"
        Map<String,Object> variables         // variables para Thymeleaf
) {}