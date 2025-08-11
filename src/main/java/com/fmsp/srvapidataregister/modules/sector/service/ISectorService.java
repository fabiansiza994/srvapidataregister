package com.fmsp.srvapidataregister.modules.sector.service;

import com.fmsp.srvapidataregister.modules.sector.dto.SectorDTO;

public interface ISectorService {
    SectorDTO findSectorById(Long id);
}
