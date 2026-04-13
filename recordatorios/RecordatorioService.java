package com.dentalflow.dentalflow.recordatorios;

import com.dentalflow.dentalflow.dto.RecordatorioResponse;
import com.dentalflow.dentalflow.integraciones.whatsapp.WhatsAppConversationService;
import com.dentalflow.dentalflow.integraciones.whatsapp.WhatsAppSendResult;
import com.dentalflow.dentalflow.model.Turno;
import com.dentalflow.dentalflow.repository.RecordatorioEnvioRepository;
import com.dentalflow.dentalflow.repository.TurnoRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecordatorioService {

    private final TurnoRepository turnoRepository;
    private final RecordatorioEnvioRepository envioRepository;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final WhatsAppConversationService whatsAppConversationService;

    public RecordatorioService(TurnoRepository turnoRepository,
                               RecordatorioEnvioRepository envioRepository,
                               ObjectProvider<JavaMailSender> mailSenderProvider,
                               WhatsAppConversationService whatsAppConversationService) {
        this.turnoRepository = turnoRepository;
        this.envioRepository = envioRepository;
        this.mailSenderProvider = mailSenderProvider;
        this.whatsAppConversationService = whatsAppConversationService;
    }

    public List<RecordatorioResponse> proximos(int horas) {
        LocalDateTime desde = LocalDateTime.now();
        LocalDateTime hasta = desde.plusHours(Math.max(horas, 1));
        return turnoRepository.findByFechaHoraInicioBetween(desde, hasta).stream()
                .map(this::aRecordatorioEmail)
                .toList();
    }

    public RecordatorioResponse enviarEmail(Long turnoId) {
        Turno turno = turno(turnoId);
        return enviarCorreo(turno, "email-paciente", turno.getPaciente() != null ? turno.getPaciente().getEmail() : null, "Recordatorio de turno Dentalflow", "El paciente no tiene email registrado");
    }

    public List<RecordatorioResponse> enviarRecordatoriosCompleto(Long turnoId) {
        Turno turno = turno(turnoId);
        return List.of(
                enviarCorreo(turno, "email-paciente", turno.getPaciente() != null ? turno.getPaciente().getEmail() : null, "Recordatorio de turno Dentalflow", "El paciente no tiene email registrado"),
                enviarCorreo(turno, "email-odontologo", turno.getOdontologo() != null ? turno.getOdontologo().getEmail() : null, "Recordatorio de turno Dentalflow - Odontólogo", "El odontólogo no tiene email registrado"),
                prepararWhatsApp(turnoId)
        );
    }

    public RecordatorioResponse enviarEmailOdontologo(Long turnoId) {
        Turno turno = turno(turnoId);
        return enviarCorreo(turno, "email-odontologo", turno.getOdontologo() != null ? turno.getOdontologo().getEmail() : null, "Recordatorio de turno Dentalflow - Odontólogo", "El odontólogo no tiene email registrado");
    }

    public RecordatorioResponse prepararWhatsApp(Long turnoId) {
        Turno turno = turno(turnoId);
        String canal = "whatsapp-paciente";
        String destinatario = turno.getPaciente() != null ? turno.getPaciente().getTelefono() : null;
        String mensaje = mensaje(turno);

        if (destinatario == null || destinatario.isBlank()) {
            return new RecordatorioResponse(turnoId, "whatsapp", "", "El paciente no tiene teléfono registrado", false);
        }

        if (envioRepository.existsByTurnoIdAndCanal(turno.getId(), canal)) {
            return new RecordatorioResponse(turno.getId(), "whatsapp", destinatario, "Ya se envió este recordatorio", false);
        }

        WhatsAppSendResult sendResult = whatsAppConversationService.enviarMensaje(destinatario, mensaje, "recordatorio-turno:" + turno.getId());
        if (sendResult.success()) {
            envioRepository.save(new RecordatorioEnvio(null, turno.getId(), canal, destinatario, LocalDateTime.now()));
        }

        String detalle = sendResult.success() ? "Enviado por WhatsApp" : "No se pudo enviar por WhatsApp: " + sendResult.error();
        return new RecordatorioResponse(turnoId, "whatsapp", destinatario, mensaje + " | " + detalle, sendResult.success());
    }

    private RecordatorioResponse aRecordatorioEmail(Turno turno) {
        String destinatario = turno.getPaciente() != null ? turno.getPaciente().getEmail() : null;
        return new RecordatorioResponse(
                turno.getId(),
                "email",
                destinatario != null ? destinatario : "",
                mensaje(turno),
                false
        );
    }

    private RecordatorioResponse enviarCorreo(Turno turno, String canal, String destinatario, String subject, String mensajeSinCorreo) {
        if (destinatario == null || destinatario.isBlank()) {
            return new RecordatorioResponse(turno.getId(), canal, "", mensajeSinCorreo, false);
        }

        if (envioRepository.existsByTurnoIdAndCanal(turno.getId(), canal)) {
            return new RecordatorioResponse(turno.getId(), canal, destinatario, "Ya se envió este recordatorio", false);
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            return new RecordatorioResponse(turno.getId(), canal, destinatario, "SMTP no configurado; recordatorio preparado", false);
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(destinatario);
        mailMessage.setSubject(subject);
        mailMessage.setText(mensaje(turno));
        mailSender.send(mailMessage);
        envioRepository.save(new RecordatorioEnvio(null, turno.getId(), canal, destinatario, LocalDateTime.now()));
        return new RecordatorioResponse(turno.getId(), canal, destinatario, mensaje(turno), true);
    }

    private String mensaje(Turno turno) {
        return "Hola " + (turno.getPaciente() != null ? turno.getPaciente().getNombre() : "")
                + ", te recordamos tu turno para el día " + turno.getFechaHoraInicio()
                + " con el odontólogo " + (turno.getOdontologo() != null ? turno.getOdontologo().getNombre() : "")
                + ".";
    }

    private Turno turno(Long turnoId) {
        return turnoRepository.findById(turnoId)
                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));
    }
}

