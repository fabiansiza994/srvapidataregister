package com.fmsp.srvapidataregister.modules.methodPayment.service;

import com.fmsp.srvapidataregister.modules.methodPayment.dto.FormaPagoCreateDTO;
import com.fmsp.srvapidataregister.modules.methodPayment.dto.FormaPagoDTO;

import java.util.List;
import java.util.Optional;

public interface IMOPService {
    Optional<FormaPagoDTO> findById(Long id);
    FormaPagoCreateDTO save(FormaPagoCreateDTO formapagoDTO);
    List<FormaPagoDTO> findAll();
    List<FormaPagoDTO> findAllByEmpresa(Long id);
    void deleteById(Long id);
    FormaPagoDTO update(FormaPagoDTO formapagoDTO);
    FormaPagoCreateDTO saveNewUser(FormaPagoCreateDTO formapagoDTO);
}