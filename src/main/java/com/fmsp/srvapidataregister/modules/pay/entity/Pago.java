package com.fmsp.srvapidataregister.modules.pay.entity;

import com.fmsp.srvapidataregister.modules.companies.entity.Empresa;
import com.fmsp.srvapidataregister.modules.users.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter @Getter
@Entity
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentId;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private String moneda;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaPago;

    @Column(nullable = false, unique = true)
    private String transactionId;

    public Pago() {}

    public Pago(String paymentId, Usuario usuario, Empresa empresa, BigDecimal monto, String moneda, String estado, LocalDateTime fechaPago, String transactionId) {
        this.paymentId = paymentId;
        this.usuario = usuario;
        this.empresa = empresa;
        this.monto = monto;
        this.moneda = moneda;
        this.estado = estado;
        this.fechaPago = fechaPago;
        this.transactionId = transactionId;
    }
}
