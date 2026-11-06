package pe.upn.perudeestacion.dto;

import java.time.LocalDate;

import pe.upn.perudeestacion.entity.AlertaNino;

public record AlertaResponse(Long id, String comunicado, LocalDate fecha, String estado, String magnitud,
        Boolean vigente) {
    public static AlertaResponse from(AlertaNino a) {
        return a == null ? null
                : new AlertaResponse(a.getId(), a.getComunicado(), a.getFecha(), a.getEstado(), a.getMagnitud(),
                        a.getVigente());
    }
}
