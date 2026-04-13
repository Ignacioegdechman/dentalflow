package com.dentalflow.dentalflow.dto;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public record ReportePeriodoResponse(
        LocalDate desde,
        LocalDate hasta,
        long pacientesNuevos,
        long turnos,
        long turnosPendientes,
        long turnosConfirmados,
        long turnosCancelados,
        Double ingresos,
        Double deudaTotal
) {
    public Map<String, Object> toMap() {
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("Desde", desde);
        datos.put("Hasta", hasta);
        datos.put("Pacientes nuevos", pacientesNuevos);
        datos.put("Turnos", turnos);
        datos.put("Turnos pendientes", turnosPendientes);
        datos.put("Turnos confirmados", turnosConfirmados);
        datos.put("Turnos cancelados", turnosCancelados);
        datos.put("Ingresos", ingresos);
        datos.put("Deuda total", deudaTotal);
        return datos;
    }
}

