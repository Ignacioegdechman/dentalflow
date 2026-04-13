package com.dentalflow.dentalflow.integraciones.whatsapp;

import com.dentalflow.dentalflow.repository.WhatsAppMessageAuditRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WhatsAppConversationService {

	private final WhatsAppMessageAuditRepository auditRepository;
	private final MetaWhatsAppSender metaSender;
	private final FallbackWhatsAppSender fallbackSender;
	private final WhatsAppProperties properties;

	public WhatsAppConversationService(WhatsAppMessageAuditRepository auditRepository,
									   MetaWhatsAppSender metaSender,
									   FallbackWhatsAppSender fallbackSender,
									   WhatsAppProperties properties) {
		this.auditRepository = auditRepository;
		this.metaSender = metaSender;
		this.fallbackSender = fallbackSender;
		this.properties = properties;
	}

	public void registrarConversacion(WhatsAppWebhookRequest request, WhatsAppWebhookResponse response) {
		String phone = normalizarTelefono(request.from());
		String inbound = Optional.ofNullable(request.message()).orElse("");
		guardar(phone, inbound, WhatsAppDirection.INBOUND, WhatsAppDeliveryStatus.RECEIVED, null, null, null, request.patientName());

		String outbound = Optional.ofNullable(response.reply()).orElse("");
		guardar(phone, outbound, WhatsAppDirection.OUTBOUND, WhatsAppDeliveryStatus.PROCESSED, "bot", null, null, response.action());
	}

	public WhatsAppSendResult enviarMensaje(String phone, String message, String metadata) {
		String destino = normalizarTelefono(phone);
		if (destino.isBlank()) {
			WhatsAppSendResult result = new WhatsAppSendResult(false, "", message, "none", null, "Telefono invalido");
			guardar(destino, message, WhatsAppDirection.OUTBOUND, WhatsAppDeliveryStatus.FAILED, result.provider(), result.providerMessageId(), result.error(), metadata);
			return result;
		}

		WhatsAppSender sender = properties.isEnabled() && properties.isUseMetaApi() ? metaSender : fallbackSender;
		WhatsAppSendResult result = sender.send(destino, message);

		guardar(
				destino,
				message,
				WhatsAppDirection.OUTBOUND,
				result.success() ? WhatsAppDeliveryStatus.SENT : WhatsAppDeliveryStatus.FAILED,
				result.provider(),
				result.providerMessageId(),
				result.error(),
				metadata
		);
		return result;
	}

	private void guardar(String phone,
						String message,
						WhatsAppDirection direction,
						WhatsAppDeliveryStatus status,
						String provider,
						String providerMessageId,
						String error,
						String metadata) {
		auditRepository.save(new WhatsAppMessageAudit(
				null,
				phone == null ? "" : phone,
				message == null ? "" : message,
				direction,
				status,
				provider,
				providerMessageId,
				error,
				metadata,
				LocalDateTime.now()
		));
	}

	private String normalizarTelefono(String raw) {
		if (raw == null) {
			return "";
		}
		return raw.replaceAll("[^0-9+]", "").trim();
	}
}


