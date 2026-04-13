package com.dentalflow.dentalflow.mapper;

import com.dentalflow.dentalflow.dto.*;
import com.dentalflow.dentalflow.model.*;

public final class EntityMapper {

    private EntityMapper() {
    }

    public static PacienteResponse toPacienteResponse(Paciente paciente) {
        return new PacienteResponse(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getTelefono(),
                paciente.getEmail(),
                paciente.getFechaNacimiento(),
                paciente.getObservaciones(),
                paciente.isActivo()
        );
    }

    public static OdontologoResponse toOdontologoResponse(Odontologo odontologo) {
        return new OdontologoResponse(
                odontologo.getId(),
                odontologo.getNombre(),
                odontologo.getEspecialidad(),
                odontologo.getTelefono(),
                odontologo.getEmail()
        );
    }

    public static TratamientoResponse toTratamientoResponse(Tratamiento tratamiento) {
        return new TratamientoResponse(
                tratamiento.getId(),
                tratamiento.getNombre(),
                tratamiento.getPrecio(),
                tratamiento.getDuracionMinutos()
        );
    }

    public static PagoResponse toPagoResponse(Pago pago) {
        return new PagoResponse(
                pago.getId(),
                pago.getPaciente() != null ? pago.getPaciente().getId() : null,
                pago.getMonto(),
                pago.getMetodo(),
                pago.getFecha(),
                pago.getEstado()
        );
    }

    public static TurnoResponse toTurnoResponse(Turno turno) {
        return new TurnoResponse(
                turno.getId(),
                turno.getPaciente() != null ? turno.getPaciente().getId() : null,
                turno.getPaciente() != null ? turno.getPaciente().getNombre() : null,
                turno.getOdontologo() != null ? turno.getOdontologo().getId() : null,
                turno.getOdontologo() != null ? turno.getOdontologo().getNombre() : null,
                turno.getTratamiento() != null ? turno.getTratamiento().getId() : null,
                turno.getTratamiento() != null ? turno.getTratamiento().getNombre() : null,
                turno.getFechaHoraInicio(),
                turno.getFechaHoraFin(),
                turno.getEstado()
        );
    }

    public static HistoriaClinicaResponse toHistoriaClinicaResponse(HistoriaClinica historia) {
        return new HistoriaClinicaResponse(
                historia.getId(),
                historia.getPaciente() != null ? historia.getPaciente().getId() : null,
                historia.getPaciente() != null ? historia.getPaciente().getNombre() : null,
                historia.getOdontologo() != null ? historia.getOdontologo().getId() : null,
                historia.getOdontologo() != null ? historia.getOdontologo().getNombre() : null,
                historia.getTratamiento() != null ? historia.getTratamiento().getId() : null,
                historia.getTratamiento() != null ? historia.getTratamiento().getNombre() : null,
                historia.getFecha(),
                historia.getDiagnostico(),
                historia.getProcedimiento(),
                historia.getObservaciones()
        );
    }
}


