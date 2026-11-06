package pe.upn.perudeestacion.dto;

import pe.upn.perudeestacion.entity.CentroApoyo;

public record CentroResponse(Long id, Long programaId, String programa, String departamento, String nombre,
        String direccion, String distrito, String telefono) {
    public static CentroResponse from(CentroApoyo c) {
        return new CentroResponse(c.getId(), c.getPrograma().getId(), c.getPrograma().getNombre(),
                c.getDepartamento().getCodigo(), c.getNombre(), c.getDireccion(), c.getDistrito(), c.getTelefono());
    }
}
