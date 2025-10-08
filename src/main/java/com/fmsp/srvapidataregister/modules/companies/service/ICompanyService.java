package com.fmsp.srvapidataregister.modules.companies.service;

import com.fmsp.srvapidataregister.modules.companies.dto.CompanySettingsDTO;

public interface ICompanyService {
    CompanySettingsDTO getSettings(Long empresaId, String idTx);
    CompanySettingsDTO setAllowView(Long empresaId, boolean enabled, String idTx);
    CompanySettingsDTO setAllowEdit(Long empresaId, boolean enabled, String idTx);
    CompanySettingsDTO updateSettings(Long empresaId, boolean allowView, boolean allowEdit, String idTx);
}
