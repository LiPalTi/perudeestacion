package pe.upn.perudeestacion.dto;

import java.util.List;

import pe.upn.perudeestacion.entity.ImpactoRegion;
import pe.upn.perudeestacion.entity.NivelRiesgo;

public record ImpactoResponse(Long id, Long alertaId, String departamento, String sector, NivelRiesgo nivelRiesgo,
        String descripcion, List<AlimentoAfectado> alimentos) {

    public record AlimentoAfectado(Long alimentoId, String alimento, NivelRiesgo riesgoAlza) {
    }

    public static ImpactoResponse from(ImpactoRegion i) {
        return new ImpactoResponse(i.getId(), i.getAlerta().getId(), i.getDepartamento().getCodigo(), i.getSector(),
                i.getNivelRiesgo(), i.getDescripcion(),
                i.getAlimentos().stream()
                        .map(ia -> new AlimentoAfectado(ia.getAlimento().getId(), ia.getAlimento().getNombre(),
                                ia.getRiesgoAlza()))
                        .toList());
    }
}
