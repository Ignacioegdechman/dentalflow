package com.dentalflow.dentalflow.repository;

import com.dentalflow.dentalflow.enums.EstadoPago;
import com.dentalflow.dentalflow.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long>, JpaSpecificationExecutor<Pago> {

    List<Pago> findByPacienteId(Long pacienteId);

    Page<Pago> findByPacienteId(Long pacienteId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.monto),0) FROM Pago p WHERE p.paciente.id = :pacienteId AND p.estado = :estado")
    Double deudaPorPaciente(@Param("pacienteId") Long pacienteId, @Param("estado") EstadoPago estado);

    @Query("SELECT COALESCE(SUM(p.monto),0) FROM Pago p WHERE p.estado = :estado")
    Double totalIngresos(@Param("estado") EstadoPago estado);

    @Query("SELECT COALESCE(SUM(p.monto),0) FROM Pago p WHERE p.estado = :estado")
    Double totalDeudaPorEstado(@Param("estado") EstadoPago estado);
}