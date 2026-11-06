package pe.upn.perudeestacion.dto;

import pe.upn.perudeestacion.entity.Alimento;
import pe.upn.perudeestacion.entity.Categoria;

public record AlimentoResponse(Long id, String nombre, Categoria categoria, String descripcion, String imagenUrl) {
    public static AlimentoResponse from(Alimento a) {
        return new AlimentoResponse(a.getId(), a.getNombre(), a.getCategoria(), a.getDescripcion(), a.getImagenUrl());
    }
}
