package com.fmsp.srvapidataregister.modules.jobs.dto;

import com.fmsp.srvapidataregister.modules.clients.dto.ClientePlanoDTO;
import com.fmsp.srvapidataregister.modules.methodPayment.dto.FormaPagoDTO;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import com.fmsp.srvapidataregister.modules.users.dto.UsuarioSimpleDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class TrabajoListDTO {
    private Long id;
    private LocalDate fecha;
    private BigDecimal valorTotal;
    private String descripcionLabor;
    private ClientePlanoDTO cliente;     // nombre/identificacion
    private FormaPagoDTO formaPago;      // nombre forma
    private UsuarioSimpleDTO usuario;          // quien lo creó
    private String estado;
    private String foto1;
    private PacienteDTO paciente;
}
