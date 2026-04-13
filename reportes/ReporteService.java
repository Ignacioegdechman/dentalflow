package com.dentalflow.dentalflow.reportes;

import com.dentalflow.dentalflow.dto.ReporteResumenResponse;
import com.dentalflow.dentalflow.dto.ReportePeriodoResponse;
import com.dentalflow.dentalflow.enums.EstadoPago;
import com.dentalflow.dentalflow.enums.EstadoTurno;
import com.dentalflow.dentalflow.repository.PacienteRepository;
import com.dentalflow.dentalflow.repository.PagoRepository;
import com.dentalflow.dentalflow.repository.TurnoRepository;
import com.dentalflow.dentalflow.specification.PacienteSpecifications;
import com.dentalflow.dentalflow.specification.PagoSpecifications;
import com.dentalflow.dentalflow.specification.TurnoSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReporteService {

    private final PacienteRepository pacienteRepository;
    private final TurnoRepository turnoRepository;
    private final PagoRepository pagoRepository;

    public ReporteService(PacienteRepository pacienteRepository,
                          TurnoRepository turnoRepository,
                          PagoRepository pagoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.turnoRepository = turnoRepository;
        this.pagoRepository = pagoRepository;
    }

    public ReporteResumenResponse resumen() {
        return new ReporteResumenResponse(
                pacienteRepository.countByActivoTrue(),
                pacienteRepository.count(),
                turnoRepository.countByFechaHoraInicioBetween(LocalDate.now().atStartOfDay(), LocalDate.now().atTime(java.time.LocalTime.MAX)),
                turnoRepository.countByEstado(EstadoTurno.PENDIENTE),
                turnoRepository.countByEstado(EstadoTurno.CONFIRMADO),
                turnoRepository.countByEstado(EstadoTurno.CANCELADO),
                pagoRepository.totalIngresos(EstadoPago.PAGADO),
                pagoRepository.totalDeudaPorEstado(EstadoPago.PENDIENTE)
        );
    }

    public ReportePeriodoResponse resumenPeriodo(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(LocalTime.MAX);

        Specification<com.dentalflow.dentalflow.model.Paciente> pacientesSpec = PacienteSpecifications.creadoEntre(inicio, fin);
        Specification<com.dentalflow.dentalflow.model.Turno> turnosSpec = TurnoSpecifications.fechaEntre(inicio, fin);
        Specification<com.dentalflow.dentalflow.model.Pago> pagosSpec = PagoSpecifications.fechaEntre(desde, hasta);

        long pacientesNuevos = pacienteRepository.count(pacientesSpec);
        List<com.dentalflow.dentalflow.model.Turno> turnos = turnoRepository.findAll(turnosSpec);
        List<com.dentalflow.dentalflow.model.Pago> pagos = pagoRepository.findAll(pagosSpec);

        long pendientes = turnos.stream().filter(t -> t.getEstado() == EstadoTurno.PENDIENTE).count();
        long confirmados = turnos.stream().filter(t -> t.getEstado() == EstadoTurno.CONFIRMADO).count();
        long cancelados = turnos.stream().filter(t -> t.getEstado() == EstadoTurno.CANCELADO).count();
        Double ingresos = pagos.stream().filter(p -> p.getEstado() == EstadoPago.PAGADO).mapToDouble(p -> p.getMonto() != null ? p.getMonto() : 0).sum();
        Double deuda = pagos.stream().filter(p -> p.getEstado() == EstadoPago.PENDIENTE).mapToDouble(p -> p.getMonto() != null ? p.getMonto() : 0).sum();

        return new ReportePeriodoResponse(
                desde,
                hasta,
                pacientesNuevos,
                turnos.size(),
                pendientes,
                confirmados,
                cancelados,
                ingresos,
                deuda
        );
    }
}

