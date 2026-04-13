package com.dentalflow.dentalflow.integraciones.whatsapp;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MetaWhatsAppSender implements WhatsAppSender {

    private final WhatsAppProperties properties;
    private final RestClient restClient;

    public MetaWhatsAppSender(WhatsAppProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder().build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public WhatsAppSendResult sendText(String to, String message) {
        if (!properties.isCloudEnabled() || blank(properties.getAccessToken()) || blank(properties.getPhoneNumberId())) {
            return new WhatsAppSendResult(false, null, "WhatsApp Cloud API no configurada");
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("to", normalizePhone(to));
            payload.put("type", "text");
            payload.put("text", Map.of("body", message));

            Map<String, Object> response = restClient.post()
                    .uri(properties.getApiBase() + "/" + properties.getPhoneNumberId() + "/messages")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                return new WhatsAppSendResult(false, null, "Respuesta vacía de WhatsApp");
            }

            Object messages = response.get("messages");
            if (messages instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof Map<?, ?> first) {
                Object id = first.get("id");
                return new WhatsAppSendResult(true, id != null ? id.toString() : null, null);
            }

            return new WhatsAppSendResult(true, null, null);
        } catch (Exception ex) {
            return new WhatsAppSendResult(false, null, ex.getMessage());
        }
    }

    private String normalizePhone(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replaceAll("[^0-9]", "");
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}

