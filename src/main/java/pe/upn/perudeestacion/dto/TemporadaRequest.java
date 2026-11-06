package pe.upn.perudeestacion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pe.upn.perudeestacion.entity.NivelTemporada;

public record TemporadaRequest(
        @NotNull(message = "alimentoId es obligatorio") @Positive Long alimentoId,
        @NotNull(message = "departamentoId es obligatorio") @Positive Long departamentoId,
        @NotNull(message = "mes es obligatorio")
        @Min(value = 1, message = "mes debe estar entre 1 y 12")
        @Max(value = 12, message = "mes debe estar entre 1 y 12")
        Integer mes,
        @NotNull(message = "nivel es obligatorio (ALTA, MEDIA o BAJA)") NivelTemporada nivel) {
}
