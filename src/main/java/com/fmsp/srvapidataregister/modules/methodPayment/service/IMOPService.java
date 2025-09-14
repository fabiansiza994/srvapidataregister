package com.fmsp.srvapidataregister.modules.methodPayment.service;

import com.fmsp.srvapidataregister.modules.methodPayment.dto.FormaPagoDTO;

import java.util.Optional;

public interface IMOPService {
    Optional<FormaPagoDTO> findById(Long id);
}
