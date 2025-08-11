package com.fmsp.srvapidataregister.modules.pais.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Setter @Getter
public class Pais {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(name = "codigo_pais", nullable = false, unique = true)
    private String codigoPais; // Ej: "CO", "MX", "AR"
}