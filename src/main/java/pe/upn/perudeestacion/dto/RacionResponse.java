package pe.upn.perudeestacion.dto;

import java.math.BigDecimal;
import java.util.List;

public record RacionResponse(Long simulacionId, String departamento, int kcalDiariasHogar, int kcalTotal, int dias,
        List<Grupo> lista, BigDecimal costoTotalEstimado, List<String> recomendaciones, List<Apoyo> apoyo) {

    public record Grupo(String grupo, BigDecimal kg, BigDecimal costoEstimado) {
    }

    public record Apoyo(Long programaId, String programa, String urlOficial, long centrosEnDepartamento) {
    }
}
