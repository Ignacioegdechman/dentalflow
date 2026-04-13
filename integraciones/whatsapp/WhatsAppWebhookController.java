package com.dentalflow.dentalflow.integraciones.whatsapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/webhooks/whatsapp")
public class WhatsAppWebhookController {

    private final WhatsAppConversationService conversationService;
    private final WhatsAppWebhookSignatureVerifier signatureVerifier;
    private final String webhookToken;

    public WhatsAppWebhookController(WhatsAppConversationService conversationService,
                                      WhatsAppWebhookSignatureVerifier signatureVerifier,
                                      @Value("${app.whatsapp.webhook-token:dev-whatsapp-token}") String webhookToken) {
        this.conversationService = conversationService;
        this.signatureVerifier = signatureVerifier;
        this.webhookToken = webhookToken;
    }

    @GetMapping
    public ResponseEntity<String> verificar(@RequestParam(name = "hub.verify_token", required = false) String verifyToken,
                                            @RequestParam(name = "hub.challenge", required = false) String challenge) {
        if (challenge == null) {
            return ResponseEntity.badRequest().body("Missing challenge");
        }
        if (verifyToken == null || !verifyToken.equals(webhookToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid verify token");
        }
        return ResponseEntity.ok(challenge);
    }

    @PostMapping
    public ResponseEntity<WhatsAppWebhookResponse> recibir(@RequestBody String rawPayload,
                                                           @RequestHeader(name = "X-Webhook-Token", required = false) String token,
                                                           @RequestHeader(name = "X-Hub-Signature-256", required = false) String signatureHeader) {
        if (!signatureVerifier.isValid(rawPayload, signatureHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new WhatsAppWebhookResponse(false, "auth", "Firma inválida"));
        }

        if ((signatureHeader == null || signatureHeader.isBlank()) && (token == null || !token.equals(webhookToken))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new WhatsAppWebhookResponse(false, "auth", "Token inválido"));
        }

        try {
            WhatsAppWebhookRequest request = parseRequest(rawPayload);
            return ResponseEntity.ok(conversationService.handleInbound(request, rawPayload));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new WhatsAppWebhookResponse(false, "payload", "Formato de webhook inválido"));
        }
    }

    private WhatsAppWebhookRequest parseRequest(String rawPayload) {
        boolean metaFormat = rawPayload != null && rawPayload.contains("\"entry\"");
        String from = extract(rawPayload, "\\\"from\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");
        String message = metaFormat
                ? extract(rawPayload, "\\\"body\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"")
                : extract(rawPayload, "\\\"message\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");

        if (from.isBlank() && message.isBlank()) {
            throw new IllegalArgumentException("Sin campos de mensaje");
        }
        return new WhatsAppWebhookRequest(from, message, null);
    }

    private String extract(String raw, String regex) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        Matcher matcher = Pattern.compile(regex).matcher(raw);
        return matcher.find() ? matcher.group(1) : "";
    }
}

