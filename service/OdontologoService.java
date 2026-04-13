package com.dentalflow.dentalflow.service;

import com.dentalflow.dentalflow.dto.OdontologoRequest;
import com.dentalflow.dentalflow.dto.OdontologoResponse;
import com.dentalflow.dentalflow.mapper.EntityMapper;
import com.dentalflow.dentalflow.model.Odontologo;
import com.dentalflow.dentalflow.repository.OdontologoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OdontologoService {

    private final OdontologoRepository repository;

    public OdontologoService(OdontologoRepository repository) {
        this.repository = repository;
    }

    public List<OdontologoResponse> obtenerTodos() {
        return repository.findAll().stream().map(EntityMapper::toOdontologoResponse).toList();
    }

    public OdontologoResponse crear(OdontologoRequest request) {
        Odontologo odontologo = new Odontologo();
        odontologo.setNombre(request.nombre());
        odontologo.setEspecialidad(request.especialidad());
        odontologo.setTelefono(request.telefono());
        odontologo.setEmail(request.email());
        return EntityMapper.toOdontologoResponse(repository.save(odontologo));
    }

}