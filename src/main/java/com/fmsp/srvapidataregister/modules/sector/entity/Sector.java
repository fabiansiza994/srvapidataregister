package com.fmsp.srvapidataregister.modules.sector.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Setter @Getter
public class Sector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre; // Ej: "Medico", "Tecnologia", e

    @Column
    private String descripcion;
}