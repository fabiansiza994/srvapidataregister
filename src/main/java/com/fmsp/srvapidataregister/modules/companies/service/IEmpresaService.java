package com.fmsp.srvapidataregister.modules.companies.service;

import com.fmsp.srvapidataregister.modules.companies.dto.EmpresaDTO;

public interface IEmpresaService {
    EmpresaDTO save(EmpresaDTO empresaDTO);
    EmpresaDTO findById(Long id);
    EmpresaDTO findByNombre(String nombre);
}
