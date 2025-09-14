package com.fmsp.srvapidataregister.modules.pais.service;

import com.fmsp.srvapidataregister.modules.pais.dto.PaisDTO;

import java.util.List;

public interface IPaisService {
    PaisDTO findById(Long id);
    List<PaisDTO> findAllActive();
}
