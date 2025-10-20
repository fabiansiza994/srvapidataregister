package com.fmsp.srvapidataregister.modules.pay.dto;

import com.fmsp.srvapidataregister.modules.pay.model.Period;
import com.fmsp.srvapidataregister.modules.pay.model.PlanId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class SubscribeRequest {
    @NotNull
    private PlanId planId;
    @NotNull
    private Period period; // MONTHLY or ANNUAL
    @NotNull
    @Email
    private String payerEmail;

    // Opcional: para resolver moneda según el país de la empresa
    private Long empresaId;

    public PlanId getPlanId() { return planId; }
    public void setPlanId(PlanId planId) { this.planId = planId; }

    public Period getPeriod() { return period; }
    public void setPeriod(Period period) { this.period = period; }

    public String getPayerEmail() { return payerEmail; }
    public void setPayerEmail(String payerEmail) { this.payerEmail = payerEmail; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
}