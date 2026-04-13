package com.dentalflow.dentalflow.integraciones.whatsapp;

import com.dentalflow.dentalflow.repository.WhatsAppMessageAuditRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Validated
@RestController
@RequestMapping("/integraciones/whatsapp")
public class WhatsAppAdminController {

	private final WhatsAppConversationService conversationService;
	private final WhatsAppMessageAuditRepository auditRepository;

	public WhatsAppAdminController(WhatsAppConversationService conversationService,
								   WhatsAppMessageAuditRepository auditRepository) {
		this.conversationService = conversationService;
		this.auditRepository = auditRepository;
	}

	@GetMapping("/auditoria")
	public Page<WhatsAppAuditResponse> auditoria(@RequestParam(required = false) String phone,
												 @RequestParam(required = false) WhatsAppDirection direction,
												 @RequestParam(required = false) WhatsAppDeliveryStatus status,
												 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
												 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
												 Pageable pageable) {

		Specification<WhatsAppMessageAudit> spec = Specification.where(WhatsAppAuditSpecifications.phoneContains(phone))
				.and(WhatsAppAuditSpecifications.directionEquals(direction))
				.and(WhatsAppAuditSpecifications.statusEquals(status))
				.and(WhatsAppAuditSpecifications.createdAtBetween(from, to));

		return auditRepository.findAll(spec, pageable).map(WhatsAppAuditResponse::from);
	}

	@PostMapping("/enviar-prueba")
	public WhatsAppSendResult enviarPrueba(@RequestBody SendTestMessageRequest request) {
		return conversationService.enviarMensaje(request.phone(), request.message(), "manual-test");
	}

	public record SendTestMessageRequest(
			@NotBlank String phone,
			@NotBlank String message
	) {
	}
}


