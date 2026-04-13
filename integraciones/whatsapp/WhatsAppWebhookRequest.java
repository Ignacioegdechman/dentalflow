package com.dentalflow.dentalflow.integraciones.whatsapp;

public record WhatsAppWebhookRequest(
        String from,
        String message,
        String patientName
) {
}

