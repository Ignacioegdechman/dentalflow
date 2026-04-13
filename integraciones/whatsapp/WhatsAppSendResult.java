package com.dentalflow.dentalflow.integraciones.whatsapp;

public record WhatsAppSendResult(
        boolean success,
        String providerMessageId,
        String error
) {
}

