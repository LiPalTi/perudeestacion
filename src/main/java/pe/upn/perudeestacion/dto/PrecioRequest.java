package pe.upn.perudeestacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PrecioRequest(
        @NotNull @Positive Long alimentoId,
        @NotNull @Positive Long departamentoId,
        @NotNull(message = "precioKg es obligatorio")
        @DecimalMin(value = "0.01", message = "precioKg debe ser mayor que 0")
        @Digits(integer = 8, fraction = 2, message = "precioKg admite 2 decimales")
        BigDecimal precioKg,
        @NotNull @PastOrPresent(message = "La fecha no puede ser futura") LocalDate fecha,
        @NotBlank(message = "La fuente es obligatoria (por ejemplo SISAP-MIDAGRI)") @Size(max = 120) String fuente) {
}
