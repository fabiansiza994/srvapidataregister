package com.fmsp.srvapidataregister.modules.companies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanySettingsDTO {
    private Long id;
    private String nombre;
    private Boolean allowView;
    private Boolean allowEdit;
}
