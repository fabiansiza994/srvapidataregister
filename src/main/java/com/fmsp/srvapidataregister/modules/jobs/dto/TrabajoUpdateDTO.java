package com.fmsp.srvapidataregister.modules.jobs.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter @Getter
public class TrabajoUpdateDTO {
    @NotNull
    private Long id;                 // redundante con path, pero útil para validar
    @PositiveOrZero
    private Double valorLabor;
    @PositiveOrZero private Double valorMateriales;
    @PositiveOrZero private Double ganancias;
    @PositiveOrZero private Double valorTotal;
    private LocalDate fecha;
    @Size(max = 2000)
    private String descripcionLabor;

    // Asociaciones (opcionales: si vienen, se actualizan)
    private Long clienteId;
    private Long formaPagoId;
    private Long pacienteId; // opcional (solo sector salud)

    private String estado;
    // Flags de eliminación de fotos
    private boolean eliminarFoto1;
    private boolean eliminarFoto2;
    private boolean eliminarFoto3;
    private boolean eliminarFoto4;
}
