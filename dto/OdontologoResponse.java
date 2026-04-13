package com.dentalflow.dentalflow.dto;

import java.time.LocalDateTime;

public record OdontologoResponse(
        Long id,
        String nombre,
        String especialidad,
        String telefono,
        String email
) {
}


