package pe.upn.perudeestacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CentroRequest(
        @NotNull @Positive Long programaId,
        @NotNull @Positive Long departamentoId,
        @NotBlank @Size(max = 120) String nombre,
        @NotBlank @Size(max = 200) String direccion,
        @NotBlank @Size(max = 80) String distrito,
        @Pattern(regexp = "^[0-9+ ()-]{6,20}$", message = "Telefono invalido") String telefono) {
}
