package com.dentalflow.dentalflow.integraciones.sync;

import com.dentalflow.dentalflow.dto.PacienteResponse;
import com.dentalflow.dentalflow.dto.TurnoResponse;
import com.dentalflow.dentalflow.dto.TratamientoResponse;
import com.dentalflow.dentalflow.service.PacienteService;
import com.dentalflow.dentalflow.service.TratamientoService;
import com.dentalflow.dentalflow.service.TurnoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/integraciones/sync")
public class IntegracionSyncController {

    private final TurnoService turnoService;
    private final PacienteService pacienteService;
    private final TratamientoService tratamientoService;

    public IntegracionSyncController(TurnoService turnoService,
                                     PacienteService pacienteService,
                                     TratamientoService tratamientoService) {
        this.turnoService = turnoService;
        this.pacienteService = pacienteService;
        this.tratamientoService = tratamientoService;
    }

    @GetMapping("/turnos")
    public Page<TurnoResponse> turnos(Pageable pageable) {
        return turnoService.obtenerPagina(pageable);
    }

    @GetMapping("/pacientes")
    public Page<PacienteResponse> pacientes(Pageable pageable) {
        return pacienteService.obtenerPagina(pageable);
    }

    @GetMapping("/tratamientos")
    public List<TratamientoResponse> tratamientos() {
        return tratamientoService.obtenerTodos();
    }
}

