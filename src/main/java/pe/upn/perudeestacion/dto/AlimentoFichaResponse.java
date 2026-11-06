package pe.upn.perudeestacion.dto;

import java.util.List;

/** Ficha del alimento: datos, consejos de conservacion y precios de referencia mas recientes. */
public record AlimentoFichaResponse(AlimentoResponse alimento, List<ConsejoResponse> consejos,
        List<PrecioResponse> preciosRecientes) {
}
