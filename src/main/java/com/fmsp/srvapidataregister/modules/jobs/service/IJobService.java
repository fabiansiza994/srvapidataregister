package com.fmsp.srvapidataregister.modules.jobs.service;

import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoCreateDTO;
import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoDTO;
import com.fmsp.srvapidataregister.modules.jobs.entity.Trabajo;
import org.springframework.web.multipart.MultipartFile;

public interface IJobService {
    TrabajoDTO create(TrabajoCreateDTO dto,
                      MultipartFile foto1, MultipartFile foto2,
                      MultipartFile foto3, MultipartFile foto4,
                      String idTx);
}
