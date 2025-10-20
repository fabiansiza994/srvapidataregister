package com.fmsp.srvapidataregister.modules.pay.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.companies.entity.Empresa;
import com.fmsp.srvapidataregister.modules.companies.repository.EmpresaRepository;
import com.fmsp.srvapidataregister.modules.pay.config.MercadoPagoProperties;
import com.fmsp.srvapidataregister.modules.pay.dto.SubscribeRequest;
import com.fmsp.srvapidataregister.modules.pay.dto.SubscribeResponse;
import com.fmsp.srvapidataregister.modules.pay.model.Period;
import com.fmsp.srvapidataregister.modules.pay.model.PlanDefinition;
import com.fmsp.srvapidataregister.modules.pay.model.PlanId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("pay")
public class BillingPlanController {

    private final MercadoPagoProperties props;
    private final EmpresaRepository empresaRepository;
    private final com.fmsp.srvapidataregister.modules.pay.service.MercadoPagoClient mpClient;

    public BillingPlanController(MercadoPagoProperties props, EmpresaRepository empresaRepository,
                                 com.fmsp.srvapidataregister.modules.pay.service.MercadoPagoClient mpClient) {
        this.props = props;
        this.empresaRepository = empresaRepository;
        this.mpClient = mpClient;
    }

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<Object>> listPlans() {
        String idTx = UUID.randomUUID().toString();
        String currency = props.getCurrency() != null ? props.getCurrency() : "USD"; // TODO confirmar moneda
        List<PlanDefinition> plans = buildFrontendAlignedPlans(currency);
        return ResponseHandler.successResponse(plans, idTx);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<Object>> subscribe(@RequestBody @Valid SubscribeRequest req) {
        String idTx = UUID.randomUUID().toString();
        String currency = resolveCurrency(req.getEmpresaId());

        // Validar plan
        Optional<PlanDefinition> plan = findPlan(buildFrontendAlignedPlans(currency), req.getPlanId());
        if (plan.isEmpty()) {
            var map = new HashMap<String, Object>();
            map.put("error", "PLAN_NOT_FOUND");
            map.put("planId", String.valueOf(req.getPlanId()));
            return ResponseHandler.badRequestResponse(java.util.List.of(), idTx);
        }

        // Monto según el periodo
        PlanDefinition p = plan.get();
        BigDecimal amount = (req.getPeriod() == Period.ANNUAL) ? p.getAnnualPrice() : p.getMonthlyPrice();
        if (amount == null) {
            var map = new HashMap<String, Object>();
            map.put("error", "PRICE_UNAVAILABLE_FOR_CURRENCY");
            map.put("currency", currency);
            return ResponseHandler.badRequestResponse(java.util.List.of(), idTx);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            // Plan gratuito: no requiere redirección al checkout
            SubscribeResponse resp = new SubscribeResponse();
            resp.setProvider("mercadopago");
            resp.setMode(Boolean.TRUE.equals(props.getSandbox()) ? "sandbox" : "live");
            resp.setNextAction("none");
            resp.setPlanId(req.getPlanId());
            resp.setPeriod(req.getPeriod());
            resp.setCurrency(currency);
            resp.setAmount(amount);
            resp.setMessage("Plan gratuito, no requiere pago.");
            return ResponseHandler.successResponse(resp, idTx);
        }

        // Crear SUSCRIPCIÓN (preapproval) con el cliente de MP
        var init = mpClient.createSubscriptionPreapproval(
                null,                 // preapprovalPlanId (usamos auto_recurring)
                p.getName(),          // title visible
                currency,
                amount,
                req.getPayerEmail(),
                req.getPeriod().name()
        );

        SubscribeResponse resp = new SubscribeResponse();
        resp.setProvider("mercadopago");
        resp.setMode(Boolean.TRUE.equals(props.getSandbox()) ? "sandbox" : "live");
        resp.setNextAction("redirect");
        resp.setRedirectUrl(init.initPoint());
        resp.setPlanId(req.getPlanId());
        resp.setPeriod(req.getPeriod());
        resp.setCurrency(currency);
        resp.setAmount(amount);
        resp.setMessage("Suscripción creada. Redirige al checkout de Mercado Pago.");

        return ResponseHandler.successResponse(resp, idTx);
    }

    private List<PlanDefinition> buildFrontendAlignedPlans(String currency) {
        List<PlanDefinition> plans = new ArrayList<>();

        // FREE
        plans.add(new PlanDefinition(
                PlanId.FREE,
                "free",
                "Free",
                "Ideal para comenzar",
                currency,
                calcMonthlyPrice(currency, 0),
                calcAnnualPrice(calcMonthlyPrice(currency, 0)),
                0,
                false,
                Map.of(
                        "maxUsers", 3,
                        "maxImagesPerJob", 4,
                        "maxClientes", 100,
                        "maxPacientes", 100,
                        "maxTrabajos", 200
                )
        ));

        // BASIC (50 -> 50.000 COP)
        plans.add(new PlanDefinition(
                PlanId.BASIC,
                "basic",
                "Basic",
                "Para equipos pequeños",
                currency,
                calcMonthlyPrice(currency, 50),
                calcAnnualPrice(calcMonthlyPrice(currency, 50)),
                50,
                false,
                Map.of(
                        "maxUsers", 6,
                        "maxImagesPerJob", 6,
                        "maxClientes", 500,
                        "maxPacientes", 500,
                        "maxTrabajos", 1000
                )
        ));

        // ADVANCE (80 -> 80.000 COP)
        plans.add(new PlanDefinition(
                PlanId.PRO, // interno; publicId alinea con el front
                "advance",
                "Advance",
                "Más capacidad y control",
                currency,
                calcMonthlyPrice(currency, 80),
                calcAnnualPrice(calcMonthlyPrice(currency, 80)),
                80,
                true,
                Map.of(
                        "maxUsers", 25,
                        "maxImagesPerJob", 8,
                        "maxClientes", 1000,
                        "maxPacientes", 1000,
                        "maxTrabajos", 2000
                )
        ));

        // ENTERPRISE (100 -> 100.000 COP)
        plans.add(new PlanDefinition(
                PlanId.ENTERPRISE,
                "enterprise",
                "Enterprise",
                "Empresas con alta demanda",
                currency,
                calcMonthlyPrice(currency, 100),
                calcAnnualPrice(calcMonthlyPrice(currency, 100)),
                100,
                false,
                Map.of(
                        "maxUsers", 100,
                        "maxImagesPerJob", 10,
                        "maxClientes", 2000,
                        "maxPacientes", 2000,
                        "maxTrabajos", 3000
                )
        ));

        return plans;
    }

    private BigDecimal calcMonthlyPrice(String currency, int priceCopUnits) {
        if ("COP".equalsIgnoreCase(currency)) {
            return BigDecimal.valueOf(priceCopUnits).multiply(BigDecimal.valueOf(1000));
        }
        // TODO: si la moneda es USD, definir conversión (por ahora null para que el front resuelva)
        return null;
    }

    private BigDecimal calcAnnualPrice(BigDecimal monthlyPrice) {
        if (monthlyPrice == null) return null;
        // 12 meses con 20% de descuento
        return monthlyPrice
                .multiply(BigDecimal.valueOf(12))
                .multiply(BigDecimal.valueOf(0.8))
                .setScale(0, java.math.RoundingMode.HALF_UP);
    }

    private Optional<PlanDefinition> findPlan(List<PlanDefinition> plans, PlanId id) {
        return plans.stream().filter(p -> p.getId() == id).findFirst();
    }

    private String resolveCurrency(Long empresaId) {
        // Fallback por configuración
        String fallback = props.getCurrency() != null ? props.getCurrency() : "USD";
        if (empresaId == null) return fallback;
        return empresaRepository.findById(empresaId)
                .map(Empresa::getPais)
                .map(p -> {
                    String code = p.getCodigoPais();
                    if ("CO".equalsIgnoreCase(code)) return "COP";
                    if ("US".equalsIgnoreCase(code) || "USA".equalsIgnoreCase(code)) return "USD";
                    // Otros países a futuro: MX->MXN, AR->ARS, CL->CLP, PE->PEN, etc.
                    return fallback;
                })
                .orElse(fallback);
    }
}
