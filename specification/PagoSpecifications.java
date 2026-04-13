package com.dentalflow.dentalflow.specification;

import com.dentalflow.dentalflow.enums.EstadoPago;
import com.dentalflow.dentalflow.enums.MetodoPago;
import com.dentalflow.dentalflow.model.Pago;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class PagoSpecifications {

    private PagoSpecifications() {
    }

    public static Specification<Pago> pacienteIdEquals(Long pacienteId) {
        return (root, query, cb) -> pacienteId == null ? cb.conjunction() : cb.equal(root.get("paciente").get("id"), pacienteId);
    }

    public static Specification<Pago> estadoEquals(EstadoPago estado) {
        return (root, query, cb) -> estado == null ? cb.conjunction() : cb.equal(root.get("estado"), estado);
    }

    public static Specification<Pago> metodoEquals(MetodoPago metodo) {
        return (root, query, cb) -> metodo == null ? cb.conjunction() : cb.equal(root.get("metodo"), metodo);
    }

    public static Specification<Pago> fechaEntre(LocalDate desde, LocalDate hasta) {
        return (root, query, cb) -> {
            if (desde == null && hasta == null) {
                return cb.conjunction();
            }
            if (desde == null) {
                return cb.lessThanOrEqualTo(root.get("fecha"), hasta);
            }
            if (hasta == null) {
                return cb.greaterThanOrEqualTo(root.get("fecha"), desde);
            }
            return cb.between(root.get("fecha"), desde, hasta);
        };
    }
}

