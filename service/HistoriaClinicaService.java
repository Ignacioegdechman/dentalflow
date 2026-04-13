package com.dentalflow.dentalflow.service;

import com.dentalflow.dentalflow.dto.HistoriaClinicaRequest;
import com.dentalflow.dentalflow.dto.HistoriaClinicaResponse;
import com.dentalflow.dentalflow.mapper.EntityMapper;
import com.dentalflow.dentalflow.model.HistoriaClinica;
import com.dentalflow.dentalflow.model.Odontologo;
import com.dentalflow.dentalflow.model.Paciente;
import com.dentalflow.dentalflow.model.Tratamiento;
import com.dentalflow.dentalflow.repository.HistoriaClinicaRepository;
import com.dentalflow.dentalflow.repository.OdontologoRepository;
import com.dentalflow.dentalflow.repository.PacienteRepository;
import com.dentalflow.dentalflow.repository.TratamientoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HistoriaClinicaService {

    private final HistoriaClinicaRepository repository;
    private final PacienteRepository pacienteRepository;
    private final OdontologoRepository odontologoRepository;
    private final TratamientoRepository tratamientoRepository;

    public HistoriaClinicaService(HistoriaClinicaRepository repository,
                                  PacienteRepository pacienteRepository,
                                  OdontologoRepository odontologoRepository,
                                  TratamientoRepository tratamientoRepository) {
        this.repository = repository;
        this.pacienteRepository = pacienteRepository;
        this.odontologoRepository = odontologoRepository;
        this.tratamientoRepository = tratamientoRepository;
    }

    public List<HistoriaClinicaResponse> obtenerTodos() {
        return repository.findAll().stream().map(EntityMapper::toHistoriaClinicaResponse).toList();
    }

    public Page<HistoriaClinicaResponse> obtenerPorPaciente(Long pacienteId, Pageable pageable) {
        return repository.findByPacienteId(pacienteId, pageable).map(EntityMapper::toHistoriaClinicaResponse);
    }

    public HistoriaClinicaResponse crear(HistoriaClinicaRequest request) {
        Paciente paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));
        Odontologo odontologo = odontologoRepository.findById(request.odontologoId())
                .orElseThrow(() -> new NoSuchElementException("Odontólogo no encontrado"));
        Tratamiento tratamiento = tratamientoRepository.findById(request.tratamientoId())
                .orElseThrow(() -> new NoSuchElementException("Tratamiento no encontrado"));

        HistoriaClinica historia = new HistoriaClinica();
        historia.setPaciente(paciente);
        historia.setOdontologo(odontologo);
        historia.setTratamiento(tratamiento);
        historia.setFecha(request.fecha());
        historia.setDiagnostico(request.diagnostico());
        historia.setProcedimiento(request.procedimiento());
        historia.setObservaciones(request.observaciones());

        return EntityMapper.toHistoriaClinicaResponse(repository.save(historia));
    }

}