package com.dentalflow.dentalflow.integraciones.whatsapp;

public interface WhatsAppSender {

    WhatsAppSendResult sendText(String to, String message);
}

