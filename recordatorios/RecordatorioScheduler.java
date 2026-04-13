package com.dentalflow.dentalflow.recordatorios;

import com.dentalflow.dentalflow.enums.EstadoTurno;
import com.dentalflow.dentalflow.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RecordatorioScheduler {

    private final TurnoRepository turnoRepository;
    private final RecordatorioService recordatorioService;
    private final boolean enabled;
    private final int horas;

    public RecordatorioScheduler(TurnoRepository turnoRepository,
                                 RecordatorioService recordatorioService,
                                 @Value("${app.recordatorios.enabled:false}") boolean enabled,
                                 @Value("${app.recordatorios.horas:24}") int horas) {
        this.turnoRepository = turnoRepository;
        this.recordatorioService = recordatorioService;
        this.enabled = enabled;
        this.horas = horas;
    }

    @Scheduled(cron = "${app.recordatorios.cron:0 0 * * * *}")
    public void ejecutar() {
        if (!enabled) {
            return;
        }

        LocalDateTime desde = LocalDateTime.now();
        LocalDateTime hasta = desde.plusHours(Math.max(horas, 1));

        turnoRepository.findByFechaHoraInicioBetween(desde, hasta).stream()
                .filter(turno -> turno.getEstado() == EstadoTurno.PENDIENTE || turno.getEstado() == EstadoTurno.CONFIRMADO)
                .forEach(turno -> recordatorioService.enviarRecordatoriosCompleto(turno.getId()));
    }
}




