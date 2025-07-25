package com.fmsp.srvapidataregister.modules.jobs.entity;

import com.fmsp.srvapidataregister.modules.clients.entity.Cliente;
import com.fmsp.srvapidataregister.modules.methodPayment.entity.FormaPago;
import com.fmsp.srvapidataregister.modules.users.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter @Getter
@Entity
public class Trabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private BigDecimal valorLabor;
    private BigDecimal valorMateriales;
    private BigDecimal valorTotal;
    private BigDecimal ganancias;

    @Column(length = 500)
    private String descripcionLabor;

    // Guardar imágenes en Base64
    @Column(length = 1000)
    private String foto1;

    @Column(length = 1000)
    private String foto2;

    @Column(length = 1000)
    private String foto3;

    @Column(length = 1000)
    private String foto4;

    @ManyToOne
    @JoinColumn(name = "CLIENTE_ID", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "FORMA_PAGO_ID", nullable = false)
    private FormaPago formaPago;

    @ManyToOne
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;
}
