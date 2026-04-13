package com.dentalflow.dentalflow.integraciones.whatsapp;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class MetaWhatsAppSender implements WhatsAppSender {

	private final RestTemplate restTemplate = new RestTemplate();
	private final WhatsAppProperties properties;

	public MetaWhatsAppSender(WhatsAppProperties properties) {
		this.properties = properties;
	}

	@Override
	public WhatsAppSendResult send(String destinationPhone, String message) {
		if (!properties.isUseMetaApi()) {
			return new WhatsAppSendResult(false, destinationPhone, message, "meta", null, "Meta API deshabilitada");
		}

		if (isBlank(properties.getAccessToken()) || isBlank(properties.getPhoneNumberId())) {
			return new WhatsAppSendResult(false, destinationPhone, message, "meta", null, "Credenciales de Meta incompletas");
		}

		String url = properties.getApiUrl() + "/v19.0/" + properties.getPhoneNumberId() + "/messages";

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(properties.getAccessToken());
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> body = new HashMap<>();
		body.put("messaging_product", "whatsapp");
		body.put("to", destinationPhone);
		body.put("type", "text");
		body.put("text", Map.of("body", message));

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
			boolean ok = response.getStatusCode().is2xxSuccessful();
			return new WhatsAppSendResult(ok, destinationPhone, message, "meta", null, ok ? null : "Meta devolvió estado " + response.getStatusCode());
		} catch (Exception ex) {
			return new WhatsAppSendResult(false, destinationPhone, message, "meta", null, ex.getMessage());
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}


