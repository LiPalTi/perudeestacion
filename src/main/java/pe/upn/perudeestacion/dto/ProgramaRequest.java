package pe.upn.perudeestacion.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProgramaRequest(
        @NotBlank @Size(max = 120) String nombre,
        @NotBlank @Size(max = 120) String entidad,
        @NotBlank @Size(max = 300)
        @Pattern(regexp = "^https?://.+", message = "urlOficial debe ser una URL http o https")
        String urlOficial,
        @NotNull @PastOrPresent LocalDate fechaRevision,
        @NotEmpty(message = "Debe registrar al menos un requisito")
        List<@NotBlank @Size(max = 300) String> requisitos) {
}
