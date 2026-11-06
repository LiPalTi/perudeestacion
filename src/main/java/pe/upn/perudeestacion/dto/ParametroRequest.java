package pe.upn.perudeestacion.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ParametroRequest(@NotNull @DecimalMin(value = "0.0", message = "valor no puede ser negativo") BigDecimal valor) {
}
