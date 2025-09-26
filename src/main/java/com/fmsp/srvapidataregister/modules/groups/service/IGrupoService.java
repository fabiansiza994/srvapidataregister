package com.fmsp.srvapidataregister.modules.groups.service;

import com.fmsp.srvapidataregister.modules.groups.dto.*;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IGrupoService {
    GrupoDTO save(GrupoDTO grupoDTO);
    GrupoDetailDTO create(GrupoCreateDTO dto, String idTx);
    GrupoDetailDTO update(Long id, GrupoUpdateDTO dto, String idTx);
    void delete(Long id, String idTx);

    Page<GrupoListDTO> list(int page, int size, String sortBy, String direction);
    Page<GrupoListDTO> search(String q, int page, int size, String sortBy, String direction);

    Optional<GrupoDetailDTO> findDetail(Long id, String idTx);
}
