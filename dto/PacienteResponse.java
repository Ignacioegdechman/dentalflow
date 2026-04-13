package com.dentalflow.dentalflow.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PacienteResponse(
        Long id,
        String nombre,
        String telefono,
        String email,
        LocalDate fechaNacimiento,
        String observaciones,
        boolean activo
) {
}


