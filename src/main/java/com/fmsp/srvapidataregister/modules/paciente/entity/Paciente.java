package com.fmsp.srvapidataregister.modules.paciente.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private String documento; // DNI, cédula, etc.
    private String telefono;
    private String email;
    private String direccion;
    @Column(name = "cliente_id")
    private Long clienteId;
    private String estado = "ACTIVO";
}