package com.fmsp.srvapidataregister.modules.groups.entity;

import com.fmsp.srvapidataregister.modules.companies.entity.Empresa;
import com.fmsp.srvapidataregister.modules.users.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Entity
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grupo_seq")
    @SequenceGenerator(name = "grupo_seq", sequenceName = "grupo_sequence", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa; // Relación con la empresa

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Usuario> usuarios;

    public Grupo(Long id, String nombre, Set<Usuario> usuarios) {
        this.id = id;
        this.nombre = nombre;
        this.usuarios = usuarios;
    }

    public Grupo() {}

}
