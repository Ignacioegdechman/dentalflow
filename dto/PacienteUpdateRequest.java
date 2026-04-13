package com.dentalflow.dentalflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PacienteUpdateRequest(
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 30) String telefono,
        @Email @Size(max = 150) String email,
        @Past LocalDate fechaNacimiento,
        String observaciones
) {
}

