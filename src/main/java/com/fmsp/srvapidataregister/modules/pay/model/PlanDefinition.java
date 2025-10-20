package com.fmsp.srvapidataregister.modules.pay.model;

import java.math.BigDecimal;
import java.util.Map;

public class PlanDefinition {
    private PlanId id;              // Identificador interno (enum)
    private String publicId;        // Identificador usado por el front: free/basic/advance/enterprise
    private String name;
    private String description;
    private String currency; // ISO 4217

    // Precios generales (compatibilidad/hints para el front)
    private BigDecimal monthlyPrice; // Si moneda = COP, se calcula como priceCop * 1000
    private BigDecimal annualPrice;  // TODO: definir precios anuales si aplica

    // Esquema de frontend
    private Integer priceCop;        // Unidades en miles, p. ej., 30 => 30.000 COP
    private Boolean popular;         // Opcional
    private Map<String, Integer> limits; // maxUsers, maxImagesPerJob, etc.

    public PlanDefinition() {}

    public PlanDefinition(PlanId id, String publicId, String name, String description, String currency,
                          BigDecimal monthlyPrice, BigDecimal annualPrice,
                          Integer priceCop, Boolean popular, Map<String, Integer> limits) {
        this.id = id;
        this.publicId = publicId;
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.monthlyPrice = monthlyPrice;
        this.annualPrice = annualPrice;
        this.priceCop = priceCop;
        this.popular = popular;
        this.limits = limits;
    }

    public PlanId getId() { return id; }
    public void setId(PlanId id) { this.id = id; }

    public String getPublicId() { return publicId; }
    public void setPublicId(String publicId) { this.publicId = publicId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }

    public BigDecimal getAnnualPrice() { return annualPrice; }
    public void setAnnualPrice(BigDecimal annualPrice) { this.annualPrice = annualPrice; }

    public Integer getPriceCop() { return priceCop; }
    public void setPriceCop(Integer priceCop) { this.priceCop = priceCop; }

    public Boolean getPopular() { return popular; }
    public void setPopular(Boolean popular) { this.popular = popular; }

    public Map<String, Integer> getLimits() { return limits; }
    public void setLimits(Map<String, Integer> limits) { this.limits = limits; }
}