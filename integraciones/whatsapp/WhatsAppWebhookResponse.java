package com.dentalflow.dentalflow.integraciones.whatsapp;

public record WhatsAppWebhookResponse(
        boolean success,
        String action,
        String reply
) {
}

