package com.dentalflow.dentalflow.repository;

import com.dentalflow.dentalflow.recordatorios.RecordatorioEnvio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordatorioEnvioRepository extends JpaRepository<RecordatorioEnvio, Long> {

    boolean existsByTurnoIdAndCanal(Long turnoId, String canal);
}

