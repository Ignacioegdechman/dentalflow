package com.dentalflow.dentalflow.integraciones.whatsapp;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class WhatsAppWebhookSignatureVerifier {

	public boolean isValid(String payload, String receivedSignature, String secret) {
		if (secret == null || secret.isBlank()) {
			return true;
		}
		if (receivedSignature == null || receivedSignature.isBlank() || payload == null) {
			return false;
		}

		String expected = "sha256=" + hmacSha256Hex(payload, secret);
		return constantTimeEquals(expected, receivedSignature);
	}

	private String hmacSha256Hex(String payload, String secret) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : hash) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception ex) {
			return "";
		}
	}

	private boolean constantTimeEquals(String a, String b) {
		if (a == null || b == null || a.length() != b.length()) {
			return false;
		}
		int diff = 0;
		for (int i = 0; i < a.length(); i++) {
			diff |= a.charAt(i) ^ b.charAt(i);
		}
		return diff == 0;
	}
}


