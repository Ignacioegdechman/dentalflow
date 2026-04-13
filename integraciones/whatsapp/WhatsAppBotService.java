package com.dentalflow.dentalflow.integraciones.whatsapp;

import com.dentalflow.dentalflow.dto.TurnoRequest;
import com.dentalflow.dentalflow.dto.TurnoResponse;
import com.dentalflow.dentalflow.enums.EstadoTurno;
import com.dentalflow.dentalflow.model.Odontologo;
import com.dentalflow.dentalflow.model.Paciente;
import com.dentalflow.dentalflow.model.Tratamiento;
import com.dentalflow.dentalflow.model.Turno;
import com.dentalflow.dentalflow.repository.OdontologoRepository;
import com.dentalflow.dentalflow.repository.PacienteRepository;
import com.dentalflow.dentalflow.repository.TratamientoRepository;
import com.dentalflow.dentalflow.repository.TurnoRepository;
import com.dentalflow.dentalflow.service.TurnoService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.StringJoiner;

@Service
public class WhatsAppBotService {

    private static final DateTimeFormatter HUMAN_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final PacienteRepository pacienteRepository;
    private final TratamientoRepository tratamientoRepository;
    private final OdontologoRepository odontologoRepository;
    private final TurnoRepository turnoRepository;
    private final TurnoService turnoService;

    public WhatsAppBotService(PacienteRepository pacienteRepository,
                              TratamientoRepository tratamientoRepository,
                              OdontologoRepository odontologoRepository,
                              TurnoRepository turnoRepository,
                              TurnoService turnoService) {
        this.pacienteRepository = pacienteRepository;
        this.tratamientoRepository = tratamientoRepository;
        this.odontologoRepository = odontologoRepository;
        this.turnoRepository = turnoRepository;
        this.turnoService = turnoService;
    }

    public WhatsAppWebhookResponse procesar(WhatsAppWebhookRequest request) {
        try {
            String telefono = normalizarTelefono(request.from());
            String texto = request.message() == null ? "" : request.message().trim();

            Optional<Paciente> pacienteOpt = pacienteRepository.findByTelefono(telefono);
            if (pacienteOpt.isEmpty()) {
                return new WhatsAppWebhookResponse(false, "registro", "Hola. Tu número no está registrado como paciente. Por favor contactá al consultorio para asociarlo.");
            }

            Paciente paciente = pacienteOpt.get();
            String lower = texto.toLowerCase(Locale.ROOT);

            if (lower.startsWith("tratamientos") || lower.equals("menu")) {
                return new WhatsAppWebhookResponse(true, "tratamientos", listarTratamientos());
            }

            if (lower.startsWith("mis turnos")) {
                return new WhatsAppWebhookResponse(true, "turnos", listarTurnosPaciente(telefono, paciente.getNombre()));
            }

            if (lower.startsWith("confirmar ")) {
                return new WhatsAppWebhookResponse(true, "confirmar", confirmarTurno(paciente, texto));
            }

            if (lower.startsWith("cancelar ")) {
                return new WhatsAppWebhookResponse(true, "cancelar", cancelarTurno(paciente, texto));
            }

            if (lower.startsWith("reprogramar ")) {
                return new WhatsAppWebhookResponse(true, "reprogramar", reprogramarTurno(paciente, texto));
            }

            if (lower.startsWith("turno")) {
                return new WhatsAppWebhookResponse(true, "agendar", agendarTurno(paciente, texto));
            }

            return new WhatsAppWebhookResponse(true, "ayuda", mensajeAyuda(paciente.getNombre()));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return new WhatsAppWebhookResponse(false, "error", ex.getMessage());
        } catch (Exception ex) {
            return new WhatsAppWebhookResponse(false, "error", "No pude procesar tu mensaje en este momento. Intentá de nuevo en unos minutos.");
        }
    }

    private String agendarTurno(Paciente paciente, String texto) {
        String[] partes = texto.split("\\s+");
        if (partes.length < 3) {
            return mensajeAyuda(paciente.getNombre()) + "\n\nTratamientos disponibles:\n" + listarTratamientos();
        }

        Long tratamientoId = parseLong(partes[1]);
        LocalDateTime fechaHora = parseFechaHora(String.join(" ", java.util.Arrays.copyOfRange(partes, 2, partes.length)));
        if (tratamientoId == null || fechaHora == null) {
            return "Formato inválido. Usá: turno <tratamientoId> <yyyy-MM-dd HH:mm>";
        }

        Tratamiento tratamiento = tratamientoRepository.findById(tratamientoId)
                .orElse(null);
        if (tratamiento == null) {
            return "No encontré ese tratamiento. Escribí 'tratamientos' para ver los disponibles.";
        }

        Odontologo odontologo = odontologoDisponible(fechaHora, tratamiento.getDuracionMinutos());
        if (odontologo == null) {
            return "No hay odontólogos disponibles en ese horario. Probá con otra fecha u hora.";
        }

        TurnoResponse turno = turnoService.crear(new TurnoRequest(
                paciente.getId(),
                odontologo.getId(),
                tratamiento.getId(),
                fechaHora,
                EstadoTurno.PENDIENTE
        ));

        return "Turno reservado con éxito.\n" +
                "ID: " + turno.id() + "\n" +
                "Paciente: " + paciente.getNombre() + "\n" +
                "Odontólogo: " + turno.odontologoNombre() + "\n" +
                "Tratamiento: " + turno.tratamientoNombre() + "\n" +
                "Fecha: " + formatoHumano(turno.fechaHoraInicio());
    }

    private String confirmarTurno(Paciente paciente, String texto) {
        Long turnoId = parseId(texto, "confirmar");
        if (turnoId == null) {
            return "Formato inválido. Usá: confirmar <turnoId>";
        }
        if (!turnoPerteneceAlPaciente(turnoId, paciente)) {
            return "No encontré un turno tuyo con ese ID.";
        }
        return validarYResponder(turnoService.confirmar(turnoId), "confirmado");
    }

    private String cancelarTurno(Paciente paciente, String texto) {
        Long turnoId = parseId(texto, "cancelar");
        if (turnoId == null) {
            return "Formato inválido. Usá: cancelar <turnoId>";
        }
        if (!turnoPerteneceAlPaciente(turnoId, paciente)) {
            return "No encontré un turno tuyo con ese ID.";
        }
        return validarYResponder(turnoService.cancelar(turnoId), "cancelado");
    }

    private String reprogramarTurno(Paciente paciente, String texto) {
        String[] partes = texto.split("\\s+");
        if (partes.length < 3) {
            return "Formato inválido. Usá: reprogramar <turnoId> <yyyy-MM-dd HH:mm>";
        }

        Long turnoId = parseLong(partes[1]);
        LocalDateTime fechaHora = parseFechaHora(String.join(" ", java.util.Arrays.copyOfRange(partes, 2, partes.length)));
        if (turnoId == null || fechaHora == null) {
            return "Formato inválido. Usá: reprogramar <turnoId> <yyyy-MM-dd HH:mm>";
        }

        if (!turnoPerteneceAlPaciente(turnoId, paciente)) {
            return "No encontré un turno tuyo con ese ID.";
        }

        TurnoResponse response = turnoService.reprogramar(turnoId, new com.dentalflow.dentalflow.dto.TurnoReprogramarRequest(fechaHora));
        return "Turno reprogramado con éxito. Nuevo horario: " + formatoHumano(response.fechaHoraInicio());
    }

    private String listarTurnosPaciente(String telefono, String nombrePaciente) {
        List<Turno> turnos = turnoRepository.findByPacienteTelefonoAndFechaHoraInicioAfterOrderByFechaHoraInicioAsc(telefono, LocalDateTime.now());
        if (turnos.isEmpty()) {
            return "No tenés turnos próximos registrados, " + nombrePaciente + ".";
        }

        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("Tus turnos próximos:");
        for (Turno turno : turnos) {
            joiner.add("- ID " + turno.getId() + " | " + formatoHumano(turno.getFechaHoraInicio()) + " | " + (turno.getEstado() != null ? turno.getEstado() : "SIN_ESTADO"));
        }
        return joiner.toString();
    }

    private String listarTratamientos() {
        return tratamientoRepository.findAll().stream()
                .sorted(Comparator.comparing(Tratamiento::getNombre, String.CASE_INSENSITIVE_ORDER))
                .map(t -> "- " + t.getId() + " | " + t.getNombre() + " | " + t.getDuracionMinutos() + " min")
                .reduce((a, b) -> a + "\n" + b)
                .orElse("No hay tratamientos cargados.");
    }

    private String mensajeAyuda(String nombre) {
        return "Hola " + nombre + ". Puedo ayudarte con estas opciones:\n" +
                "- tratamientos\n" +
                "- mis turnos\n" +
                "- turno <tratamientoId> <yyyy-MM-dd HH:mm>\n" +
                "- confirmar <turnoId>\n" +
                "- cancelar <turnoId>\n" +
                "- reprogramar <turnoId> <yyyy-MM-dd HH:mm>";
    }

    private Odontologo odontologoDisponible(LocalDateTime fechaHoraInicio, Integer duracionMinutos) {
        if (duracionMinutos == null) {
            return null;
        }

        LocalDateTime fechaHoraFin = fechaHoraInicio.plusMinutes(duracionMinutos);
        return odontologoRepository.findAll().stream()
                .filter(odontologo -> !turnoRepository.existsByOdontologoIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(odontologo.getId(), fechaHoraFin, fechaHoraInicio))
                .findFirst()
                .orElse(null);
    }

    private boolean turnoPerteneceAlPaciente(Long turnoId, Paciente paciente) {
        Turno turno = turnoRepository.findById(turnoId).orElse(null);
        return turno != null && turno.getPaciente() != null && turno.getPaciente().getId() != null && turno.getPaciente().getId().equals(paciente.getId());
    }

    private String validarYResponder(TurnoResponse response, String accion) {
        return "Turno " + accion + " con éxito. ID: " + response.id() + " | Fecha: " + response.fechaHoraInicio();
    }

    private Long parseId(String texto, String comando) {
        String[] partes = texto.trim().split("\\s+");
        if (partes.length < 2 || !partes[0].equalsIgnoreCase(comando)) {
            return null;
        }
        return parseLong(partes[1]);
    }

    private Long parseLong(String value) {
        try {
            return Long.parseLong(value.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseFechaHora(String raw) {
        try {
            return LocalDateTime.parse(raw.replace(" ", "T"));
        } catch (DateTimeParseException ex) {
            try {
                return LocalDateTime.of(LocalDate.parse(raw.split(" ")[0]), LocalTime.parse(raw.split(" ")[1]));
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    private String normalizarTelefono(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replaceAll("[^0-9+]", "").trim();
    }

    private String formatoHumano(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : fechaHora.format(HUMAN_FORMAT);
    }
}




