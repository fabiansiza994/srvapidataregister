package com.fmsp.srvapidataregister.modules.jobs.service;

import com.fmsp.srvapidataregister.modules.jobs.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface IJobService {
    TrabajoDTO create(TrabajoCreateDTO dto,
                      MultipartFile foto1, MultipartFile foto2,
                      MultipartFile foto3, MultipartFile foto4,
                      MultipartFile foto5, MultipartFile foto6,
                      String idTx);

    long countByCliente_Id(Long clienteId);

    void delete(Long id, String idTx);

    Page<TrabajoListDTO> searchTrabajos(String q, int page, int size, String sortBy, String direction);

    TrabajoDetailDTO getTrabajoDetail(Long trabajoId, String idTx);

    TrabajoDTO update(TrabajoUpdateDTO payload,
                      MultipartFile foto1, MultipartFile foto2, MultipartFile foto3, MultipartFile foto4,
                      MultipartFile foto5, MultipartFile foto6, String idTx);
}
