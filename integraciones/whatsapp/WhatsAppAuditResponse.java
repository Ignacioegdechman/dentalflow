package com.dentalflow.dentalflow.integraciones.whatsapp;

import java.time.LocalDateTime;

public record WhatsAppAuditResponse(
        Long id,
        String telefono,
        WhatsAppDirection direction,
        WhatsAppDeliveryStatus status,
        String action,
        String message,
        String providerMessageId,
        String error,
        LocalDateTime createdAt
) {
}

