package com.dentalflow.dentalflow.dto;

import com.dentalflow.dentalflow.enums.EstadoTurno;

import java.time.LocalDateTime;

public record TurnoResponse(
        Long id,
        Long pacienteId,
        String pacienteNombre,
        Long odontologoId,
        String odontologoNombre,
        Long tratamientoId,
        String tratamientoNombre,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        EstadoTurno estado
) {
}


