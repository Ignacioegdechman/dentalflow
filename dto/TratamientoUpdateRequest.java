package com.dentalflow.dentalflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record TratamientoUpdateRequest(
        @NotBlank String nombre,
        @PositiveOrZero Double precio,
        @Positive Integer duracionMinutos
) {
}

