package com.fmsp.srvapidataregister.modules.jobs.service;

import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoCreateDTO;
import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoDTO;
import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoDetailDTO;
import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoListDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface IJobService {
    TrabajoDTO create(TrabajoCreateDTO dto,
                      MultipartFile foto1, MultipartFile foto2,
                      MultipartFile foto3, MultipartFile foto4,
                      String idTx);

    long countByCliente_Id(Long clienteId);

    Page<TrabajoListDTO> searchTrabajos(String q, int page, int size, String sortBy, String direction);
    TrabajoDetailDTO getTrabajoDetail(Long trabajoId, String idTx);
    void deleteTrabajo(Long trabajoId, String idTx);
}
