package com.dentalflow.dentalflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record HistoriaClinicaRequest(
        @NotNull Long pacienteId,
        @NotNull Long odontologoId,
        @NotNull Long tratamientoId,
        @PastOrPresent LocalDate fecha,
        @NotBlank String diagnostico,
        String procedimiento,
        String observaciones
) {
}

