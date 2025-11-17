package com.fmsp.srvapidataregister.modules.jobs.dto;

import com.fmsp.srvapidataregister.modules.clients.dto.ClientePlanoDTO;
import com.fmsp.srvapidataregister.modules.methodPayment.dto.FormaPagoDTO;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import com.fmsp.srvapidataregister.modules.users.dto.UsuarioDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class TrabajoDTO {
    private Long id;
    private LocalDate fecha;
    private BigDecimal valorLabor;
    private BigDecimal valorMateriales;
    private BigDecimal valorTotal;
    private BigDecimal ganancias;
    private String descripcionLabor;
    private String foto1;
    private String foto2;
    private String foto3;
    private String foto4;
    private String foto5;
    private String foto6;
    private ClientePlanoDTO cliente;
    private Long paciente;
    private FormaPagoDTO formaPago;
    private UsuarioDTO usuario;
}
