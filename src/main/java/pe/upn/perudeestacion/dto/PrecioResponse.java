package pe.upn.perudeestacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import pe.upn.perudeestacion.entity.PrecioReferencia;

public record PrecioResponse(Long id, Long alimentoId, String alimento, String departamento, BigDecimal precioKg,
        LocalDate fecha, String fuente) {
    public static PrecioResponse from(PrecioReferencia p) {
        return new PrecioResponse(p.getId(), p.getAlimento().getId(), p.getAlimento().getNombre(),
                p.getDepartamento().getCodigo(), p.getPrecioKg(), p.getFecha(), p.getFuente());
    }
}
