package com.fmsp.srvapidataregister.modules.users.entity;

import com.fmsp.srvapidataregister.modules.groups.entity.Grupo;
import com.fmsp.srvapidataregister.modules.roles.entity.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @ManyToOne
    @JoinColumn(name = "grupo_id", referencedColumnName = "id")
    private Grupo grupo;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

}
