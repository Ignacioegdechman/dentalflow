package com.dentalflow.dentalflow.dto;

import com.dentalflow.dentalflow.enums.EstadoPago;
import com.dentalflow.dentalflow.enums.MetodoPago;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        Long pacienteId,
        Double monto,
        MetodoPago metodo,
        LocalDate fecha,
        EstadoPago estado
) {
}


