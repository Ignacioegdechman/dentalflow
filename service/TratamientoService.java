package com.dentalflow.dentalflow.service;

import com.dentalflow.dentalflow.dto.TratamientoRequest;
import com.dentalflow.dentalflow.dto.TratamientoResponse;
import com.dentalflow.dentalflow.dto.TratamientoUpdateRequest;
import com.dentalflow.dentalflow.mapper.EntityMapper;
import com.dentalflow.dentalflow.model.Tratamiento;
import com.dentalflow.dentalflow.repository.TratamientoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TratamientoService {

    private final TratamientoRepository repository;

    public TratamientoService(TratamientoRepository repository) {
        this.repository = repository;
    }

    public List<TratamientoResponse> obtenerTodos() {
        return repository.findAll().stream().map(EntityMapper::toTratamientoResponse).toList();
    }

    public TratamientoResponse crear(TratamientoRequest request) {
        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setNombre(request.nombre());
        tratamiento.setPrecio(request.precio());
        tratamiento.setDuracionMinutos(request.duracionMinutos());
        return EntityMapper.toTratamientoResponse(repository.save(tratamiento));
    }

    public TratamientoResponse actualizar(Long id, TratamientoUpdateRequest request) {
        Tratamiento tratamiento = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tratamiento no encontrado"));

        tratamiento.setNombre(request.nombre());
        tratamiento.setPrecio(request.precio());
        tratamiento.setDuracionMinutos(request.duracionMinutos());

        return EntityMapper.toTratamientoResponse(repository.save(tratamiento));
    }

    public List<TratamientoResponse> buscarPorNombre(String nombre) {
        return repository.findAll().stream()
                .filter(tratamiento -> tratamiento.getNombre() != null && tratamiento.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .map(EntityMapper::toTratamientoResponse)
                .toList();
    }

}