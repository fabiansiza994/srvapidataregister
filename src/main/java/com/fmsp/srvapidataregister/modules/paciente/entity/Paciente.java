package com.fmsp.srvapidataregister.modules.paciente.entity;

import com.fmsp.srvapidataregister.modules.clients.entity.Cliente;
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
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    private String estado = "ACTIVO";
}