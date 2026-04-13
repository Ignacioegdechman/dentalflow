package com.dentalflow.dentalflow.recordatorios;

import com.dentalflow.dentalflow.dto.RecordatorioResponse;
import com.dentalflow.dentalflow.integraciones.whatsapp.FallbackWhatsAppSender;
import com.dentalflow.dentalflow.integraciones.whatsapp.MetaWhatsAppSender;
import com.dentalflow.dentalflow.integraciones.whatsapp.WhatsAppProperties;
import com.dentalflow.dentalflow.integraciones.whatsapp.WhatsAppSendResult;
import com.dentalflow.dentalflow.integraciones.whatsapp.WhatsAppSender;
import com.dentalflow.dentalflow.model.Turno;
import com.dentalflow.dentalflow.recordatorios.RecordatorioEnvio;
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
    private final WhatsAppSender whatsAppSender;

    public RecordatorioService(TurnoRepository turnoRepository,
                               RecordatorioEnvioRepository envioRepository,
                               ObjectProvider<JavaMailSender> mailSenderProvider,
                               MetaWhatsAppSender metaWhatsAppSender,
                               FallbackWhatsAppSender fallbackWhatsAppSender,
                               WhatsAppProperties whatsAppProperties) {
        this.turnoRepository = turnoRepository;
        this.envioRepository = envioRepository;
        this.mailSenderProvider = mailSenderProvider;
        this.whatsAppSender = whatsAppProperties.isCloudEnabled() ? metaWhatsAppSender : fallbackWhatsAppSender;
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
                enviarCorreo(turno, "email-odontologo", turno.getOdontologo() != null ? turno.getOdontologo().getEmail() : null, "Recordatorio de turno Dentalflow - Odontólogo", "El odontólogo no tiene email registrado")
        );
    }

    public RecordatorioResponse enviarEmailOdontologo(Long turnoId) {
        Turno turno = turno(turnoId);
        return enviarCorreo(turno, "email-odontologo", turno.getOdontologo() != null ? turno.getOdontologo().getEmail() : null, "Recordatorio de turno Dentalflow - Odontólogo", "El odontólogo no tiene email registrado");
    }

    public RecordatorioResponse prepararWhatsApp(Long turnoId) {
        Turno turno = turno(turnoId);
        String destinatario = turno.getPaciente() != null ? turno.getPaciente().getTelefono() : null;
        String mensaje = mensaje(turno);

        if (destinatario == null || destinatario.isBlank()) {
            return new RecordatorioResponse(turnoId, "whatsapp", "", "El paciente no tiene teléfono registrado", false);
        }

        if (envioRepository.existsByTurnoIdAndCanal(turnoId, "whatsapp-paciente")) {
            return new RecordatorioResponse(turnoId, "whatsapp", destinatario, "Ya se envió este recordatorio", false);
        }

        WhatsAppSendResult result = whatsAppSender.sendText(destinatario, mensaje);
        if (result.success()) {
            envioRepository.save(new RecordatorioEnvio(null, turnoId, "whatsapp-paciente", destinatario, LocalDateTime.now()));
        }

        return new RecordatorioResponse(turnoId, "whatsapp", destinatario,
                result.success() ? mensaje : mensaje + " | " + (result.error() != null ? result.error() : "No enviado"),
                result.success());
    }

    public RecordatorioResponse enviarWhatsAppOdontologo(Long turnoId) {
        Turno turno = turno(turnoId);
        String destinatario = turno.getOdontologo() != null ? turno.getOdontologo().getTelefono() : null;
        String mensaje = "Recordatorio profesional: " + mensaje(turno);

        if (destinatario == null || destinatario.isBlank()) {
            return new RecordatorioResponse(turnoId, "whatsapp-odontologo", "", "El odontólogo no tiene teléfono registrado", false);
        }

        if (envioRepository.existsByTurnoIdAndCanal(turnoId, "whatsapp-odontologo")) {
            return new RecordatorioResponse(turnoId, "whatsapp-odontologo", destinatario, "Ya se envió este recordatorio", false);
        }

        WhatsAppSendResult result = whatsAppSender.sendText(destinatario, mensaje);
        if (result.success()) {
            envioRepository.save(new RecordatorioEnvio(null, turnoId, "whatsapp-odontologo", destinatario, LocalDateTime.now()));
        }

        return new RecordatorioResponse(turnoId, "whatsapp-odontologo", destinatario,
                result.success() ? mensaje : mensaje + " | " + (result.error() != null ? result.error() : "No enviado"),
                result.success());
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

