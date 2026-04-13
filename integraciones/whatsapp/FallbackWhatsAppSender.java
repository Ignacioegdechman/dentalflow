package com.dentalflow.dentalflow.integraciones.whatsapp;

import org.springframework.stereotype.Component;

@Component
public class FallbackWhatsAppSender implements WhatsAppSender {

    @Override
    public WhatsAppSendResult sendText(String to, String message) {
        return new WhatsAppSendResult(false, null, "WhatsApp Cloud API no configurada; mensaje preparado");
    }
}

