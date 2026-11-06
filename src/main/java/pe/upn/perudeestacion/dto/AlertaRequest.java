package pe.upn.perudeestacion.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record AlertaRequest(
        @NotBlank(message = "comunicado es obligatorio (ej. ENFEN 19-2026)") @Size(max = 40) String comunicado,
        @NotNull @PastOrPresent LocalDate fecha,
        @NotBlank @Size(max = 40) String estado,
        @NotBlank @Size(max = 40) String magnitud,
        @NotNull Boolean vigente) {
}
