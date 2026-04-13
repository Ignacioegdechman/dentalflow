package com.dentalflow.dentalflow.dto;

import com.dentalflow.dentalflow.enums.EstadoTurno;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TurnoRequest(
        @NotNull Long pacienteId,
        @NotNull Long odontologoId,
        @NotNull Long tratamientoId,
        @Future LocalDateTime fechaHoraInicio,
        EstadoTurno estado
) {
}

