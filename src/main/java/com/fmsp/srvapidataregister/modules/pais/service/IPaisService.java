package com.fmsp.srvapidataregister.modules.pais.service;

import com.fmsp.srvapidataregister.modules.pais.dto.PaisDTO;

public interface IPaisService {
    PaisDTO findById(Long id);
}
