package com.dentalflow.dentalflow.specification;

import com.dentalflow.dentalflow.model.Paciente;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class PacienteSpecifications {

    private PacienteSpecifications() {
    }

    public static Specification<Paciente> creadoEntre(LocalDateTime desde, LocalDateTime hasta) {
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

