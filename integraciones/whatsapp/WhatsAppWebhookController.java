package com.dentalflow.dentalflow.integraciones.whatsapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/whatsapp")
public class WhatsAppWebhookController {

    private final WhatsAppBotService botService;
    private final WhatsAppProperties properties;
    private final WhatsAppWebhookSignatureVerifier signatureVerifier;
    private final WhatsAppConversationService conversationService;

    public WhatsAppWebhookController(WhatsAppBotService botService,
                                     WhatsAppProperties properties,
                                     WhatsAppWebhookSignatureVerifier signatureVerifier,
                                     WhatsAppConversationService conversationService) {
        this.botService = botService;
        this.properties = properties;
        this.signatureVerifier = signatureVerifier;
        this.conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<String> verificar(@RequestParam(name = "hub.verify_token", required = false) String verifyToken,
                                            @RequestParam(name = "hub.challenge", required = false) String challenge) {
        if (challenge == null) {
            return ResponseEntity.badRequest().body("Missing challenge");
        }
        if (verifyToken == null || !verifyToken.equals(properties.getWebhookToken())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid verify token");
        }
        return ResponseEntity.ok(challenge);
    }

    @PostMapping
    public ResponseEntity<WhatsAppWebhookResponse> recibir(@RequestBody WhatsAppWebhookRequest request,
                                                           @RequestHeader(name = "X-Webhook-Token", required = false) String token,
                                                           @RequestHeader(name = "X-Hub-Signature-256", required = false) String signature) {
        String payload = String.valueOf(request.from()) + "|" + String.valueOf(request.message()) + "|" + String.valueOf(request.patientName());
        boolean validToken = token != null && token.equals(properties.getWebhookToken());
        boolean validSignature = signatureVerifier.isValid(payload, signature, properties.getWebhookSecret());
        if (!validToken && !validSignature) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new WhatsAppWebhookResponse(false, "auth", "Token inválido"));
        }
        WhatsAppWebhookResponse response = botService.procesar(request);
        conversationService.registrarConversacion(request, response);
        return ResponseEntity.ok(response);
    }
}

