package com.dentalflow.dentalflow.dto;

import java.time.LocalDateTime;

public record TratamientoResponse(
        Long id,
        String nombre,
        Double precio,
        Integer duracionMinutos
) {
}


