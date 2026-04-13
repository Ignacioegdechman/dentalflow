package com.dentalflow.dentalflow.repository;

import com.dentalflow.dentalflow.model.Turno;
import com.dentalflow.dentalflow.enums.EstadoTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long>, JpaSpecificationExecutor<Turno> {

    long countByFechaHoraInicioBetween(LocalDateTime inicio, LocalDateTime fin);

    long countByEstado(EstadoTurno estado);

    Page<Turno> findByFechaHoraInicioBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    List<Turno> findByPacienteTelefonoAndFechaHoraInicioAfterOrderByFechaHoraInicioAsc(String telefono, LocalDateTime fechaHoraInicio);

    List<Turno> findByPacienteTelefonoOrderByFechaHoraInicioAsc(String telefono);

    List<Turno> findByFechaHoraInicioBetween(
            java.time.LocalDateTime inicio,
            java.time.LocalDateTime fin
    );

    boolean existsByOdontologoIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(Long odontologoId, LocalDateTime fechaFin, LocalDateTime fechaInicio);

    boolean existsByPacienteIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(Long pacienteId, LocalDateTime fechaFin, LocalDateTime fechaInicio);
}