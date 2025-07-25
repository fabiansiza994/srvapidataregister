package com.fmsp.srvapidataregister.modules.methodPayment.entity;

import com.fmsp.srvapidataregister.modules.companies.entity.Empresa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@Entity
public class FormaPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String formaPago;

    public int estado = 1;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    public FormaPago(Long id, String formaPago, int estado) {
        this.id = id;
        this.formaPago = formaPago;
        this.estado = estado;
    }

    public FormaPago() {
    }
}
