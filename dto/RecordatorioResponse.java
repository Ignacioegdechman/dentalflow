package com.dentalflow.dentalflow.dto;

public record RecordatorioResponse(
        Long turnoId,
        String canal,
        String destinatario,
        String mensaje,
        boolean enviado
) {
}

