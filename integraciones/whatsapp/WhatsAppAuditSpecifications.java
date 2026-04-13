package com.dentalflow.dentalflow.integraciones.whatsapp;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class WhatsAppAuditSpecifications {

    private WhatsAppAuditSpecifications() {
    }

    public static Specification<WhatsAppMessageAudit> telefonoContains(String telefono) {
        return (root, query, cb) -> {
            if (telefono == null || telefono.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("telefono")), "%" + telefono.toLowerCase() + "%");
        };
    }

    public static Specification<WhatsAppMessageAudit> directionEquals(WhatsAppDirection direction) {
        return (root, query, cb) -> direction == null ? cb.conjunction() : cb.equal(root.get("direction"), direction);
    }

    public static Specification<WhatsAppMessageAudit> createdBetween(LocalDateTime desde, LocalDateTime hasta) {
        return (root, query, cb) -> {
            if (desde == null && hasta == null) {
                return cb.conjunction();
            }
            if (desde == null) {
                return cb.lessThanOrEqualTo(root.get("createdAt"), hasta);
            }
            if (hasta == null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), desde);
            }
            return cb.between(root.get("createdAt"), desde, hasta);
        };
    }
}

