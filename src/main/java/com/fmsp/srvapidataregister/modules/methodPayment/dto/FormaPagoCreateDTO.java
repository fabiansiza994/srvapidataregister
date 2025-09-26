package com.fmsp.srvapidataregister.modules.methodPayment.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class FormaPagoCreateDTO {
    private Long id;
    private Long empresaId;
    private String formaPago;
    public int estado = 1;
}
