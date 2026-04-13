package com.dentalflow.dentalflow.integraciones.whatsapp;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class WhatsAppWebhookSignatureVerifier {

    private final WhatsAppProperties properties;

    public WhatsAppWebhookSignatureVerifier(WhatsAppProperties properties) {
        this.properties = properties;
    }

    public boolean isValid(String rawBody, String signatureHeader) {
        if (!properties.isSignatureEnabled()) {
            return true;
        }
        if (signatureHeader == null || signatureHeader.isBlank()) {
            return false;
        }
        if (properties.getAppSecret() == null || properties.getAppSecret().isBlank()) {
            return false;
        }

        try {
            String expected = "sha256=" + hmacSha256(rawBody, properties.getAppSecret());
            return expected.equalsIgnoreCase(signatureHeader.trim());
        } catch (Exception ex) {
            return false;
        }
    }

    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            String h = Integer.toHexString(0xff & b);
            if (h.length() == 1) {
                hex.append('0');
            }
            hex.append(h);
        }
        return hex.toString();
    }
}

