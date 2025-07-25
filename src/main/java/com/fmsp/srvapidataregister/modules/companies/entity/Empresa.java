package com.fmsp.srvapidataregister.modules.companies.entity;

import com.fmsp.srvapidataregister.modules.groups.entity.Grupo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String nit;

    private String estado; // ACTIVO, INACTIVO, PENDIENTE

    @OneToMany(mappedBy = "empresa")
    private List<Grupo> grupos;

}
