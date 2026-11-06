package pe.upn.perudeestacion.dto;

import java.util.List;

/** Detalle del departamento con la alerta vigente (si la alerta tiene impactos registrados aqui). */
public record DepartamentoDetalleResponse(DepartamentoResponse departamento, AlertaResponse alertaVigente,
        List<ImpactoResponse> impactos) {
}
