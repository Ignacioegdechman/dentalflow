package com.dentalflow.dentalflow.repository;

import com.dentalflow.dentalflow.model.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {

	Page<HistoriaClinica> findByPacienteId(Long pacienteId, Pageable pageable);
}