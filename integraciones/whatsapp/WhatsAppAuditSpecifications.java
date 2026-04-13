package com.dentalflow.dentalflow.integraciones.whatsapp;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class WhatsAppAuditSpecifications {

	private WhatsAppAuditSpecifications() {
	}

	public static Specification<WhatsAppMessageAudit> phoneContains(String phone) {
		return (root, query, cb) -> phone == null || phone.isBlank()
				? cb.conjunction()
				: cb.like(cb.lower(root.get("phone")), "%" + phone.toLowerCase() + "%");
	}

	public static Specification<WhatsAppMessageAudit> directionEquals(WhatsAppDirection direction) {
		return (root, query, cb) -> direction == null
				? cb.conjunction()
				: cb.equal(root.get("direction"), direction);
	}

	public static Specification<WhatsAppMessageAudit> statusEquals(WhatsAppDeliveryStatus status) {
		return (root, query, cb) -> status == null
				? cb.conjunction()
				: cb.equal(root.get("status"), status);
	}

	public static Specification<WhatsAppMessageAudit> createdAtBetween(LocalDateTime from, LocalDateTime to) {
		return (root, query, cb) -> {
			if (from != null && to != null) {
				return cb.between(root.get("createdAt"), from, to);
			}
			if (from != null) {
				return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
			}
			if (to != null) {
				return cb.lessThanOrEqualTo(root.get("createdAt"), to);
			}
			return cb.conjunction();
		};
	}
}


