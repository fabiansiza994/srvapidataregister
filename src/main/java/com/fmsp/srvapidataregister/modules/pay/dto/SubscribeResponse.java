package com.fmsp.srvapidataregister.modules.pay.dto;

import com.fmsp.srvapidataregister.modules.pay.model.Period;
import com.fmsp.srvapidataregister.modules.pay.model.PlanId;

public class SubscribeResponse {
    private String provider; // mercadopago
    private String mode;     // stub | live
    private String nextAction; // redirect | none
    private String redirectUrl; // cuando nextAction=redirect

    private PlanId planId;
    private Period period;
    private String currency;
    private java.math.BigDecimal amount;
    private String message;

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getNextAction() { return nextAction; }
    public void setNextAction(String nextAction) { this.nextAction = nextAction; }

    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }

    public PlanId getPlanId() { return planId; }
    public void setPlanId(PlanId planId) { this.planId = planId; }

    public Period getPeriod() { return period; }
    public void setPeriod(Period period) { this.period = period; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public java.math.BigDecimal getAmount() { return amount; }
    public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
