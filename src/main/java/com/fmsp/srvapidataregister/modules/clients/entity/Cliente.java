package com.fmsp.srvapidataregister.modules.clients.entity;

import com.fmsp.srvapidataregister.modules.companies.entity.Empresa;
import com.fmsp.srvapidataregister.modules.paciente.entity.Paciente;
import com.fmsp.srvapidataregister.modules.users.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String email;
    private String direccion;
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = true) // Relación con Empresa
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) // Usuario que lo registró
    private Usuario usuario;

    private String estado = "ACTIVO";

    @ManyToMany
    @JoinTable(
            name = "cliente_paciente",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "paciente_id")
    )
    private List<Paciente> pacientes;
}
