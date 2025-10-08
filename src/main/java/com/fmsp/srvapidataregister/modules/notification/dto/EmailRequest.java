package com.fmsp.srvapidataregister.modules.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest (
        @NotBlank @Email String to,
        @NotBlank String subject,
        @NotBlank String body // plain text o HTML según endpoint
) {}
