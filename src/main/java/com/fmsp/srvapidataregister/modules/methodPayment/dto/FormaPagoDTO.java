package com.fmsp.srvapidataregister.modules.methodPayment.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class FormaPagoDTO {
    private Long id;
    private String formaPago;
    public int estado = 1;
}
