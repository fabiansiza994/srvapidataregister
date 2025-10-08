package com.fmsp.srvapidataregister.core.util;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class GenerateVerificationCode {

    public String generarCodigoVerificacion() {
        Random random = new Random();
        int codigo = 10000 + random.nextInt(90000); // 10000 a 99999
        return String.valueOf(codigo);
    }

}
