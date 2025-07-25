package com.fmsp.srvapidataregister.modules.reports.entity;

import com.fmsp.srvapidataregister.modules.companies.entity.Empresa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@Entity
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreReporte; // Ej. "Factura trabajo"

    private String piePagina;     // Ej. "Gracias por confiar..."

    private String logoUrl;       // Imagen de logo (puede ser URL de S3)

    private String marcaAguaUrl;  // Imagen opcional de marca de agua

    private String bineta;

    @ManyToOne
    private Empresa empresa;
}

