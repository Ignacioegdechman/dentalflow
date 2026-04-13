package com.dentalflow.dentalflow.integraciones.whatsapp;

import java.time.LocalDateTime;

public record WhatsAppAuditResponse(
		Long id,
		String phone,
		String message,
		WhatsAppDirection direction,
		WhatsAppDeliveryStatus status,
		String provider,
		String providerMessageId,
		String error,
		LocalDateTime createdAt
) {

	public static WhatsAppAuditResponse from(WhatsAppMessageAudit entity) {
		return new WhatsAppAuditResponse(
				entity.getId(),
				entity.getPhone(),
				entity.getMessage(),
				entity.getDirection(),
				entity.getStatus(),
				entity.getProvider(),
				entity.getProviderMessageId(),
				entity.getError(),
				entity.getCreatedAt()
		);
	}
}


