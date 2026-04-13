package com.dentalflow.dentalflow.integraciones.whatsapp;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class FallbackWhatsAppSender implements WhatsAppSender {

	@Override
	public WhatsAppSendResult send(String destinationPhone, String message) {
		if (destinationPhone == null || destinationPhone.isBlank()) {
			return new WhatsAppSendResult(false, "", message, "fallback", null, "Destino vacío");
		}

		return new WhatsAppSendResult(
				true,
				destinationPhone,
				message,
				"fallback",
				"mock-" + Instant.now().toEpochMilli(),
				null
		);
	}
}


