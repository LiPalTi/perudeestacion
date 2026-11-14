package pe.upn.perudeestacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import pe.upn.perudeestacion.entity.Categoria;
import pe.upn.perudeestacion.entity.NivelRiesgo;
import pe.upn.perudeestacion.entity.NivelTemporada;

/** Respuesta del endpoint principal GET /temporadas (RF02, RF03, RF09). */
public record TemporadaConsultaResponse(DepartamentoResponse departamento, int mes, AlertaResponse alertaVigente,
        List<Item> resultados) {

    public record Item(Long alimentoId, String nombre, Categoria categoria, String imagenUrl, NivelTemporada nivelTemporada,
            NivelRiesgo riesgoAlza, BigDecimal precioRefKg, String fuentePrecio, LocalDate fechaPrecio) {
    }
}
