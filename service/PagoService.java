package com.dentalflow.dentalflow.service;

import com.dentalflow.dentalflow.dto.PagoFilterRequest;
import com.dentalflow.dentalflow.dto.PagoRequest;
import com.dentalflow.dentalflow.dto.PagoResponse;
import com.dentalflow.dentalflow.enums.EstadoPago;
import com.dentalflow.dentalflow.model.Pago;
import com.dentalflow.dentalflow.mapper.EntityMapper;
import com.dentalflow.dentalflow.model.Paciente;
import com.dentalflow.dentalflow.repository.PagoRepository;
import com.dentalflow.dentalflow.repository.PacienteRepository;
import com.dentalflow.dentalflow.specification.PagoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PagoService {

    private final PagoRepository repository;
    private final PacienteRepository pacienteRepository;

    public PagoService(PagoRepository repository, PacienteRepository pacienteRepository) {
        this.repository = repository;
        this.pacienteRepository = pacienteRepository;
    }

    public List<PagoResponse> obtenerTodos(){
        return repository.findAll().stream().map(EntityMapper::toPagoResponse).toList();
    }

    public Page<PagoResponse> obtenerPorPaciente(Long pacienteId, Pageable pageable){
        return repository.findByPacienteId(pacienteId, pageable).map(EntityMapper::toPagoResponse);
    }

    public Page<PagoResponse> buscar(PagoFilterRequest filter, Pageable pageable) {
        Specification<Pago> specification = Specification.where(PagoSpecifications.pacienteIdEquals(filter.pacienteId()))
                .and(PagoSpecifications.estadoEquals(filter.estado()))
                .and(PagoSpecifications.metodoEquals(filter.metodo()))
                .and(PagoSpecifications.fechaEntre(filter.desde(), filter.hasta()));

        return repository.findAll(specification, pageable).map(EntityMapper::toPagoResponse);
    }

    public Double deuda(Long pacienteId){
        return repository.deudaPorPaciente(pacienteId, EstadoPago.PENDIENTE);
    }

    public Double ingresos(){
        return repository.totalIngresos(EstadoPago.PAGADO);
    }

    public PagoResponse crear(PagoRequest request){
        Paciente paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));

        Pago pago = new Pago();
        pago.setPaciente(paciente);
        pago.setMonto(request.monto());
        pago.setMetodo(request.metodo());

        if (request.fecha() == null) {
            pago.setFecha(LocalDate.now());
        } else {
            pago.setFecha(request.fecha());
        }

        if (request.estado() == null) {
            pago.setEstado(EstadoPago.PENDIENTE);
        } else {
            pago.setEstado(request.estado());
        }

        return EntityMapper.toPagoResponse(repository.save(pago));
    }
}