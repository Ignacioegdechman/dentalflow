package com.dentalflow.dentalflow.dto;

import com.dentalflow.dentalflow.enums.EstadoTurno;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record TurnoFilterRequest(
        Long pacienteId,
        Long odontologoId,
        EstadoTurno estado,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta
) {
}

