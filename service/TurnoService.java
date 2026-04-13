package com.dentalflow.dentalflow.service;

import com.dentalflow.dentalflow.dto.TurnoRequest;
import com.dentalflow.dentalflow.dto.TurnoFilterRequest;
import com.dentalflow.dentalflow.dto.TurnoReprogramarRequest;
import com.dentalflow.dentalflow.dto.TurnoResponse;
import com.dentalflow.dentalflow.mapper.EntityMapper;
import com.dentalflow.dentalflow.model.Turno;
import com.dentalflow.dentalflow.model.Odontologo;
import com.dentalflow.dentalflow.model.Paciente;
import com.dentalflow.dentalflow.model.Tratamiento;
import com.dentalflow.dentalflow.repository.OdontologoRepository;
import com.dentalflow.dentalflow.repository.PacienteRepository;
import com.dentalflow.dentalflow.repository.TurnoRepository;
import com.dentalflow.dentalflow.repository.TratamientoRepository;
import com.dentalflow.dentalflow.specification.TurnoSpecifications;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TurnoService {

    private final TurnoRepository repository;
    private final PacienteRepository pacienteRepository;
    private final OdontologoRepository odontologoRepository;
    private final TratamientoRepository tratamientoRepository;

    public TurnoService(TurnoRepository repository,
                        PacienteRepository pacienteRepository,
                        OdontologoRepository odontologoRepository,
                        TratamientoRepository tratamientoRepository) {
        this.repository = repository;
        this.pacienteRepository = pacienteRepository;
        this.odontologoRepository = odontologoRepository;
        this.tratamientoRepository = tratamientoRepository;
    }

    public List<TurnoResponse> obtenerTodos() {
        return repository.findAll().stream().map(EntityMapper::toTurnoResponse).toList();
    }

    public Page<TurnoResponse> obtenerPagina(Pageable pageable) {
        return repository.findAll(pageable).map(EntityMapper::toTurnoResponse);
    }

    public List<TurnoResponse> turnosHoy() {
        LocalDate hoy = LocalDate.now();
        return repository.findByFechaHoraInicioBetween(
                hoy.atStartOfDay(),
                hoy.atTime(LocalTime.MAX)
        ).stream().map(EntityMapper::toTurnoResponse).toList();
    }

    public Page<TurnoResponse> buscar(TurnoFilterRequest filter, Pageable pageable) {
        Specification<Turno> specification = Specification.where(TurnoSpecifications.pacienteIdEquals(filter.pacienteId()))
                .and(TurnoSpecifications.odontologoIdEquals(filter.odontologoId()))
                .and(TurnoSpecifications.estadoEquals(filter.estado()))
                .and(TurnoSpecifications.fechaEntre(filter.desde(), filter.hasta()));

        return repository.findAll(specification, pageable).map(EntityMapper::toTurnoResponse);
    }

    @Transactional
    public TurnoResponse crear(TurnoRequest request) {
        if (request.fechaHoraInicio() == null) {
            throw new IllegalArgumentException("El turno requiere fecha de inicio y tratamiento con duración");
        }

        Paciente paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));
        Odontologo odontologo = odontologoRepository.findById(request.odontologoId())
                .orElseThrow(() -> new NoSuchElementException("Odontólogo no encontrado"));
        Tratamiento tratamiento = tratamientoRepository.findById(request.tratamientoId())
                .orElseThrow(() -> new NoSuchElementException("Tratamiento no encontrado"));

        if (tratamiento.getDuracionMinutos() == null) {
            throw new IllegalArgumentException("El tratamiento no tiene duración configurada");
        }

        if (request.fechaHoraInicio().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No podés crear turnos en el pasado");
        }

        LocalDateTime fechaHoraFin = request.fechaHoraInicio().plusMinutes(tratamiento.getDuracionMinutos());

        if (repository.existsByOdontologoIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                odontologo.getId(), fechaHoraFin, request.fechaHoraInicio())) {
            throw new IllegalStateException("El odontólogo ya tiene un turno en ese horario");
        }

        if (repository.existsByPacienteIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                paciente.getId(), fechaHoraFin, request.fechaHoraInicio())) {
            throw new IllegalStateException("El paciente ya tiene un turno en ese horario");
        }

        Turno turno = new Turno();
        turno.setPaciente(paciente);
        turno.setOdontologo(odontologo);
        turno.setTratamiento(tratamiento);
        turno.setFechaHoraInicio(request.fechaHoraInicio());
        turno.setFechaHoraFin(fechaHoraFin);
        turno.setEstado(request.estado() != null ? request.estado() : com.dentalflow.dentalflow.enums.EstadoTurno.PENDIENTE);

        return EntityMapper.toTurnoResponse(repository.save(turno));
    }

    public TurnoResponse confirmar(Long id) {
        Turno turno = obtenerTurno(id);
        turno.setEstado(com.dentalflow.dentalflow.enums.EstadoTurno.CONFIRMADO);
        return EntityMapper.toTurnoResponse(repository.save(turno));
    }

    public TurnoResponse cancelar(Long id) {
        Turno turno = obtenerTurno(id);
        turno.setEstado(com.dentalflow.dentalflow.enums.EstadoTurno.CANCELADO);
        return EntityMapper.toTurnoResponse(repository.save(turno));
    }

    @Transactional
    public TurnoResponse reprogramar(Long id, TurnoReprogramarRequest request) {
        Turno turno = obtenerTurno(id);

        if (request.fechaHoraInicio() == null) {
            throw new IllegalArgumentException("La nueva fecha de inicio es obligatoria");
        }

        if (request.fechaHoraInicio().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No podés reprogramar turnos en el pasado");
        }

        if (turno.getTratamiento() == null || turno.getTratamiento().getDuracionMinutos() == null) {
            throw new IllegalStateException("El turno no tiene tratamiento o duración configurada");
        }

        LocalDateTime fechaHoraFin = request.fechaHoraInicio().plusMinutes(turno.getTratamiento().getDuracionMinutos());

        if (repository.existsByOdontologoIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                turno.getOdontologo().getId(), fechaHoraFin, request.fechaHoraInicio())) {
            throw new IllegalStateException("El odontólogo ya tiene un turno en ese horario");
        }

        if (repository.existsByPacienteIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                turno.getPaciente().getId(), fechaHoraFin, request.fechaHoraInicio())) {
            throw new IllegalStateException("El paciente ya tiene un turno en ese horario");
        }

        turno.setFechaHoraInicio(request.fechaHoraInicio());
        turno.setFechaHoraFin(fechaHoraFin);
        turno.setEstado(com.dentalflow.dentalflow.enums.EstadoTurno.PENDIENTE);

        return EntityMapper.toTurnoResponse(repository.save(turno));
    }

    private Turno obtenerTurno(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Turno no encontrado"));
    }
}