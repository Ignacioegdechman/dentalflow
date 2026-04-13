package com.dentalflow.dentalflow.integraciones.whatsapp;

import com.dentalflow.dentalflow.repository.WhatsAppMessageAuditRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class WhatsAppConversationService {

    private final WhatsAppBotService botService;
    private final WhatsAppSender sender;
    private final WhatsAppMessageAuditRepository auditRepository;

    public WhatsAppConversationService(WhatsAppBotService botService,
                                       MetaWhatsAppSender metaSender,
                                       FallbackWhatsAppSender fallbackSender,
                                       WhatsAppProperties properties,
                                       WhatsAppMessageAuditRepository auditRepository) {
        this.botService = botService;
        this.auditRepository = auditRepository;
        this.sender = properties.isCloudEnabled() ? metaSender : fallbackSender;
    }

    public WhatsAppWebhookResponse handleInbound(WhatsAppWebhookRequest request, String rawPayload) {
        String telefono = normalizePhone(request.from());

        saveAudit(new WhatsAppMessageAudit(
                null,
                telefono,
                WhatsAppDirection.INBOUND,
                WhatsAppDeliveryStatus.RECEIVED,
                "inbound",
                request.message(),
                rawPayload,
                null,
                null,
                null
        ));

        WhatsAppWebhookResponse response = botService.procesar(request);

        if (response.reply() == null || response.reply().isBlank()) {
            saveAudit(new WhatsAppMessageAudit(
                    null,
                    telefono,
                    WhatsAppDirection.OUTBOUND,
                    WhatsAppDeliveryStatus.PROCESSED,
                    response.action(),
                    "",
                    null,
                    null,
                    null,
                    null
            ));
            return response;
        }

        WhatsAppSendResult sendResult = sender.sendText(telefono, response.reply());
        saveAudit(new WhatsAppMessageAudit(
                null,
                telefono,
                WhatsAppDirection.OUTBOUND,
                sendResult.success() ? WhatsAppDeliveryStatus.SENT : WhatsAppDeliveryStatus.PREPARED,
                response.action(),
                response.reply(),
                null,
                sendResult.providerMessageId(),
                sendResult.error(),
                null
        ));

        return response;
    }

    public Page<WhatsAppAuditResponse> auditoria(String telefono,
                                                 WhatsAppDirection direction,
                                                 LocalDate desde,
                                                 LocalDate hasta,
                                                 Pageable pageable) {
        LocalDateTime from = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime to = hasta != null ? hasta.atTime(LocalTime.MAX) : null;

        Specification<WhatsAppMessageAudit> spec = Specification.where(WhatsAppAuditSpecifications.telefonoContains(telefono))
                .and(WhatsAppAuditSpecifications.directionEquals(direction))
                .and(WhatsAppAuditSpecifications.createdBetween(from, to));

        return auditRepository.findAll(spec, pageable).map(this::toResponse);
    }

    private void saveAudit(WhatsAppMessageAudit audit) {
        auditRepository.save(audit);
    }

    private WhatsAppAuditResponse toResponse(WhatsAppMessageAudit audit) {
        return new WhatsAppAuditResponse(
                audit.getId(),
                audit.getTelefono(),
                audit.getDirection(),
                audit.getStatus(),
                audit.getAction(),
                audit.getMessage(),
                audit.getProviderMessageId(),
                audit.getError(),
                audit.getCreatedAt()
        );
    }

    private String normalizePhone(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replaceAll("[^0-9+]", "").trim();
    }
}


