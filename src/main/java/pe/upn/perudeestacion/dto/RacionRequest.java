package pe.upn.perudeestacion.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.upn.perudeestacion.entity.CapacidadEconomica;
import pe.upn.perudeestacion.entity.TipoVivienda;

public record RacionRequest(
        @NotBlank(message = "departamento es obligatorio (codigo, ej. PIU)")
        @Size(min = 3, max = 3, message = "departamento es un codigo de 3 letras")
        String departamento,
        @NotNull @Min(0) @Max(20) Integer adultos,
        @NotNull @Min(0) @Max(20) Integer ninos,
        @NotNull @Min(0) @Max(20) Integer adultosMayores,
        @NotNull @Min(value = 3, message = "dias debe ser al menos 3 (caja de reserva INDECI)")
        @Max(value = 30, message = "dias admite como maximo 30") Integer dias,
        @NotNull TipoVivienda tipoVivienda,
        @NotNull CapacidadEconomica capacidadEconomica) {

    @AssertTrue(message = "El hogar debe tener al menos una persona")
    public boolean isAlMenosUnaPersona() {
        int a = adultos == null ? 0 : adultos;
        int n = ninos == null ? 0 : ninos;
        int m = adultosMayores == null ? 0 : adultosMayores;
        return a + n + m >= 1;
    }
}
