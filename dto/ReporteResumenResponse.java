package com.dentalflow.dentalflow.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public record ReporteResumenResponse(
        long pacientesActivos,
        long pacientesTotales,
        long turnosHoy,
        long turnosPendientes,
        long turnosConfirmados,
        long turnosCancelados,
        Double ingresos,
        Double deudaTotal
) {
    public Map<String, Object> toMap() {
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("Pacientes activos", pacientesActivos);
        datos.put("Pacientes totales", pacientesTotales);
        datos.put("Turnos hoy", turnosHoy);
        datos.put("Turnos pendientes", turnosPendientes);
        datos.put("Turnos confirmados", turnosConfirmados);
        datos.put("Turnos cancelados", turnosCancelados);
        datos.put("Ingresos", ingresos);
        datos.put("Deuda total", deudaTotal);
        return datos;
    }
}

