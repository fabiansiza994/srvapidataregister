package com.fmsp.srvapidataregister.modules.roles.entity;

import com.fmsp.srvapidataregister.modules.users.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre; // Ejemplo: ADMIN, USER, CLIENT

    private String descripcion;

    @OneToMany(mappedBy = "rol")
    private List<Usuario> usuarios;

}
