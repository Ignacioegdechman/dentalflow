package com.dentalflow.dentalflow.dto;

import com.dentalflow.dentalflow.enums.EstadoPago;
import com.dentalflow.dentalflow.enums.MetodoPago;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record PagoRequest(
        @NotNull Long pacienteId,
        @NotNull @PositiveOrZero Double monto,
        @NotNull MetodoPago metodo,
        @FutureOrPresent LocalDate fecha,
        EstadoPago estado
) {
}

