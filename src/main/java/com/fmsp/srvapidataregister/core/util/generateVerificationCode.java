package com.fmsp.srvapidataregister.core.util;

import java.util.Random;

public class generateVerificationCode {

    public String generarCodigoVerificacion() {
        Random random = new Random();
        int codigo = 10000 + random.nextInt(90000); // 10000 a 99999
        return String.valueOf(codigo);
    }

}
