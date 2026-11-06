package pe.upn.perudeestacion.dto;

import pe.upn.perudeestacion.entity.Departamento;
import pe.upn.perudeestacion.entity.RegionNatural;

public record DepartamentoResponse(Long id, String codigo, String nombre, RegionNatural regionNatural) {
    public static DepartamentoResponse from(Departamento d) {
        return new DepartamentoResponse(d.getId(), d.getCodigo(), d.getNombre(), d.getRegionNatural());
    }
}
