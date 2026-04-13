package com.dentalflow.dentalflow.dashboard;

import com.dentalflow.dentalflow.service.PagoService;
import com.dentalflow.dentalflow.service.TurnoService;
import com.dentalflow.dentalflow.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final PacienteRepository pacienteRepository;
    private final TurnoService turnoService;
    private final PagoService pagoService;

    public DashboardService(PacienteRepository pacienteRepository,
                            TurnoService turnoService,
                            PagoService pagoService) {
        this.pacienteRepository = pacienteRepository;
        this.turnoService = turnoService;
        this.pagoService = pagoService;
    }

    public Map<String, Object> obtenerDashboard() {

        Map<String, Object> datos = new HashMap<>();

        datos.put("pacientes", pacienteRepository.count());
        datos.put("turnosHoy", turnoService.turnosHoy().size());
        datos.put("ingresos", pagoService.ingresos());

        return datos;
    }
}