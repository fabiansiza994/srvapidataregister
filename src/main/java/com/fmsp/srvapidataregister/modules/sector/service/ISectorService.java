package com.fmsp.srvapidataregister.modules.sector.service;

import com.fmsp.srvapidataregister.modules.sector.dto.SectorDTO;

import java.util.List;

public interface ISectorService {
    SectorDTO findSectorById(Long id);

    List<SectorDTO> findAllActive();
}
