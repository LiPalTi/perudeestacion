package pe.upn.perudeestacion.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import pe.upn.perudeestacion.entity.NivelRiesgo;

public record ImpactoRequest(
        @NotNull @Positive Long alertaId,
        @NotNull @Positive Long departamentoId,
        @NotBlank @Size(max = 60) String sector,
        @NotNull NivelRiesgo nivelRiesgo,
        @Size(max = 500) String descripcion,
        @Valid List<AlimentoAfectado> alimentos) {

    public record AlimentoAfectado(@NotNull @Positive Long alimentoId, @NotNull NivelRiesgo riesgoAlza) {
    }
}
