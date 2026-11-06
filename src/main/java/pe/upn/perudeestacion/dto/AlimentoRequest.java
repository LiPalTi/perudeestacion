package pe.upn.perudeestacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pe.upn.perudeestacion.entity.Categoria;

public record AlimentoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre admite como maximo 80 caracteres")
        String nombre,

        @NotNull(message = "La categoria es obligatoria (FRUTA, VERDURA, TUBERCULO o PESCADO)")
        Categoria categoria,

        @Size(max = 500, message = "La descripcion admite como maximo 500 caracteres")
        String descripcion,

        @Size(max = 300)
        @Pattern(regexp = "^(https?://.*)?$", message = "La imagen debe ser una URL http o https")
        String imagenUrl) {
}
