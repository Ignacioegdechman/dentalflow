package com.dentalflow.dentalflow.specification;

import com.dentalflow.dentalflow.enums.EstadoTurno;
import com.dentalflow.dentalflow.model.Turno;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class TurnoSpecifications {

    private TurnoSpecifications() {
    }

    public static Specification<Turno> pacienteIdEquals(Long pacienteId) {
        return (root, query, cb) -> pacienteId == null ? cb.conjunction() : cb.equal(root.get("paciente").get("id"), pacienteId);
    }

    public static Specification<Turno> odontologoIdEquals(Long odontologoId) {
        return (root, query, cb) -> odontologoId == null ? cb.conjunction() : cb.equal(root.get("odontologo").get("id"), odontologoId);
    }

    public static Specification<Turno> estadoEquals(EstadoTurno estado) {
        return (root, query, cb) -> estado == null ? cb.conjunction() : cb.equal(root.get("estado"), estado);
    }

    public static Specification<Turno> fechaEntre(LocalDateTime desde, LocalDateTime hasta) {
        return (root, query, cb) -> {
            if (desde == null && hasta == null) {
                return cb.conjunction();
            }
            if (desde == null) {
                return cb.lessThanOrEqualTo(root.get("fechaHoraInicio"), hasta);
            }
            if (hasta == null) {
                return cb.greaterThanOrEqualTo(root.get("fechaHoraInicio"), desde);
            }
            return cb.between(root.get("fechaHoraInicio"), desde, hasta);
        };
    }
}

