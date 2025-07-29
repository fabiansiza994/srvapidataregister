package com.fmsp.srvapidataregister.modules.roles.service;

import com.fmsp.srvapidataregister.modules.roles.dto.RolDTO;

public interface IRolService {
    RolDTO save(RolDTO rolDTO);
    RolDTO findById(Long id);
}
