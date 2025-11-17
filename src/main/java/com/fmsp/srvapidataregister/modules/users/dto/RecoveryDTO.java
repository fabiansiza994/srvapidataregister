package com.fmsp.srvapidataregister.modules.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RecoveryDTO {
    private Long userId;
    @NotBlank(message = "La contraseña es requerida")
    private String password;
}