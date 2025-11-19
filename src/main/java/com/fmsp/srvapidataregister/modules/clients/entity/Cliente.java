package com.fmsp.srvapidataregister.modules.clients.entity;

import com.fmsp.srvapidataregister.modules.companies.entity.Empresa;
import com.fmsp.srvapidataregister.modules.users.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String identificacion;
    private String nombre;
    private String apellido;
    private String email;
    private String direccion;
    private String telefono;
    private String razonSocial;
    private String camaraComercio;
    private String rut;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = true) // Relación con Empresa
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) // Usuario que lo registró
    private Usuario usuario;

    private String estado = "ACTIVO";
}
