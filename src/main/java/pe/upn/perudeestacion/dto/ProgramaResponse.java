package pe.upn.perudeestacion.dto;

import java.time.LocalDate;
import java.util.List;

import pe.upn.perudeestacion.entity.ProgramaApoyo;
import pe.upn.perudeestacion.entity.Requisito;

public record ProgramaResponse(Long id, String nombre, String entidad, String urlOficial, LocalDate fechaRevision,
        List<String> requisitos) {
    public static ProgramaResponse from(ProgramaApoyo p) {
        return new ProgramaResponse(p.getId(), p.getNombre(), p.getEntidad(), p.getUrlOficial(),
                p.getFechaRevision(), p.getRequisitos().stream().map(Requisito::getDescripcion).toList());
    }
}
