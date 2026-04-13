package com.dentalflow.dentalflow.dto;

import com.dentalflow.dentalflow.enums.EstadoPago;
import com.dentalflow.dentalflow.enums.MetodoPago;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record PagoFilterRequest(
        Long pacienteId,
        EstadoPago estado,
        MetodoPago metodo,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
) {
}

