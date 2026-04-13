package com.dentalflow.dentalflow.service;

import com.dentalflow.dentalflow.dto.PacienteRequest;
import com.dentalflow.dentalflow.dto.PacienteResponse;
import com.dentalflow.dentalflow.dto.PacienteUpdateRequest;
import com.dentalflow.dentalflow.mapper.EntityMapper;
import com.dentalflow.dentalflow.model.Paciente;
import com.dentalflow.dentalflow.repository.PacienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    public List<PacienteResponse> obtenerTodos() {
        return repository.findByActivoTrue().stream().map(EntityMapper::toPacienteResponse).toList();
    }

    public Page<PacienteResponse> obtenerPagina(Pageable pageable) {
        return repository.findByActivoTrue(pageable).map(EntityMapper::toPacienteResponse);
    }

    public List<PacienteResponse> buscar(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre).stream().map(EntityMapper::toPacienteResponse).toList();
    }

    public PacienteResponse crear(PacienteRequest request) {
        Paciente paciente = new Paciente();
        paciente.setNombre(request.nombre());
        paciente.setTelefono(request.telefono());
        paciente.setEmail(request.email());
        paciente.setFechaNacimiento(request.fechaNacimiento());
        paciente.setObservaciones(request.observaciones());

        return EntityMapper.toPacienteResponse(repository.save(paciente));
    }

    public PacienteResponse actualizar(Long id, PacienteUpdateRequest request) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));

        paciente.setNombre(request.nombre());
        paciente.setTelefono(request.telefono());
        paciente.setEmail(request.email());
        paciente.setFechaNacimiento(request.fechaNacimiento());
        paciente.setObservaciones(request.observaciones());

        return EntityMapper.toPacienteResponse(repository.save(paciente));
    }

    public PacienteResponse reactivar(Long id) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));

        paciente.setActivo(true);
        return EntityMapper.toPacienteResponse(repository.save(paciente));
    }

    public void eliminar(Long id) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));

        paciente.setActivo(false);
        repository.save(paciente);
    }
}