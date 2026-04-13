package com.dentalflow.dentalflow.repository;

import com.dentalflow.dentalflow.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Long>, JpaSpecificationExecutor<Paciente> {

    List<Paciente> findByActivoTrue();

    java.util.Optional<Paciente> findByTelefono(String telefono);

    long countByActivoTrue();

    Page<Paciente> findByActivoTrue(Pageable pageable);

    List<Paciente> findByNombreContainingIgnoreCase(String nombre);
}