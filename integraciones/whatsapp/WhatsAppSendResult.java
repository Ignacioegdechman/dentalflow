package com.dentalflow.dentalflow.integraciones.whatsapp;

public record WhatsAppSendResult(
		boolean success,
		String destination,
		String message,
		String provider,
		String providerMessageId,
		String error
) {
}


