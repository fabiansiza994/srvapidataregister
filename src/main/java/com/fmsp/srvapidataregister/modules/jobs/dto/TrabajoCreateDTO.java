package com.fmsp.srvapidataregister.modules.jobs.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;


public record TrabajoCreateDTO(
        @NotNull
        LocalDate fecha,
        @NotNull @PositiveOrZero
        BigDecimal valorLabor,
        @PositiveOrZero BigDecimal valorMateriales,
        @NotNull @PositiveOrZero BigDecimal valorTotal,
        @NotNull @PositiveOrZero BigDecimal ganancias,
        @Size(max = 500) String descripcionLabor,
        @NotNull Long clienteId,
        Long pacienteId,
        @NotNull Long formaPagoId
) {

}
