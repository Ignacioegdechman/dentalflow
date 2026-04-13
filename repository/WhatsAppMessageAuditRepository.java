package com.dentalflow.dentalflow.repository;

import com.dentalflow.dentalflow.integraciones.whatsapp.WhatsAppMessageAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WhatsAppMessageAuditRepository extends JpaRepository<WhatsAppMessageAudit, Long>, JpaSpecificationExecutor<WhatsAppMessageAudit> {
}

