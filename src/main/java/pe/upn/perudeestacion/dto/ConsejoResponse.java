package pe.upn.perudeestacion.dto;

import pe.upn.perudeestacion.entity.ConsejoConservacion;

public record ConsejoResponse(String metodo, String descripcion, Integer duracionDias) {
    public static ConsejoResponse from(ConsejoConservacion c) {
        return new ConsejoResponse(c.getMetodo(), c.getDescripcion(), c.getDuracionDias());
    }
}
