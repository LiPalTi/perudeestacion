package pe.upn.perudeestacion.dto;

import java.util.List;

import pe.upn.perudeestacion.entity.NivelTemporada;

/** Calendario anual de un alimento en un departamento: 12 meses, nivel null si no hay dato. */
public record CalendarioResponse(Long alimentoId, String alimento, String departamento, List<MesNivel> meses) {
    public record MesNivel(int mes, NivelTemporada nivel) {
    }
}
