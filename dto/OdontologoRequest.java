package com.dentalflow.dentalflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OdontologoRequest(
        @NotBlank @Size(max = 150) String nombre,
        @NotBlank @Size(max = 120) String especialidad,
        @NotBlank @Size(max = 30) String telefono,
        @Email @Size(max = 150) String email
) {
}

