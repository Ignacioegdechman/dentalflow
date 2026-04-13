package com.dentalflow.dentalflow.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TurnoReprogramarRequest(
        @NotNull @Future LocalDateTime fechaHoraInicio
) {
}

