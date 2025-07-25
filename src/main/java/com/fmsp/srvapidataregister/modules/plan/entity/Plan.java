package com.fmsp.srvapidataregister.modules.plan.entity;

import com.fmsp.srvapidataregister.modules.companies.entity.Empresa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter @Getter
@Entity
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private Double valor;

    @Column(name = "fecha_vigencia", nullable = false)
    private LocalDate fechaVigencia;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    // Constructores
    public Plan() {}

    public Plan(String nombre, Double valor, LocalDate fechaVigencia, Empresa empresa) {
        this.nombre = nombre;
        this.valor = valor;
        this.fechaVigencia = fechaVigencia;
        this.empresa = empresa;
    }
}
