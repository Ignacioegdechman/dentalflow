package com.dentalflow.dentalflow.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HistoriaClinicaResponse(
        Long id,
        Long pacienteId,
        String pacienteNombre,
        Long odontologoId,
        String odontologoNombre,
        Long tratamientoId,
        String tratamientoNombre,
        LocalDate fecha,
        String diagnostico,
        String procedimiento,
        String observaciones
) {
}


