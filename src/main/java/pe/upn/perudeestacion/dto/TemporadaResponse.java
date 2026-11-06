package pe.upn.perudeestacion.dto;

import pe.upn.perudeestacion.entity.Categoria;
import pe.upn.perudeestacion.entity.NivelTemporada;
import pe.upn.perudeestacion.entity.Temporada;

public record TemporadaResponse(Long id, Long alimentoId, String alimento, Categoria categoria,
        String departamento, Integer mes, NivelTemporada nivel) {
    public static TemporadaResponse from(Temporada t) {
        return new TemporadaResponse(t.getId(), t.getAlimento().getId(), t.getAlimento().getNombre(),
                t.getAlimento().getCategoria(), t.getDepartamento().getCodigo(), t.getMes(), t.getNivel());
    }
}
