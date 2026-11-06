package pe.upn.perudeestacion.dto;

import java.math.BigDecimal;

import pe.upn.perudeestacion.entity.ParametroSimulador;

public record ParametroResponse(String clave, BigDecimal valor, String descripcion) {
    public static ParametroResponse from(ParametroSimulador p) {
        return new ParametroResponse(p.getClave(), p.getValor(), p.getDescripcion());
    }
}
