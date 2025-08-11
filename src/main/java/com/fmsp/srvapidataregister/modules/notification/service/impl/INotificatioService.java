package com.fmsp.srvapidataregister.modules.notification.service.impl;

public interface INotificatioService {
    void enviarCodigoVerificacionWhatsapp(String numeroDestino, String codigo);
    void enviarCodigoVerificacionMail(String email, String codigo);
}
