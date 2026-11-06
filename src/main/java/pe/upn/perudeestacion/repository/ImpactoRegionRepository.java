package pe.upn.perudeestacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.upn.perudeestacion.entity.ImpactoRegion;

public interface ImpactoRegionRepository extends JpaRepository<ImpactoRegion, Long> {

    @Query("""
            SELECT DISTINCT i FROM ImpactoRegion i
            JOIN FETCH i.alerta
            JOIN FETCH i.departamento
            LEFT JOIN FETCH i.alimentos ia
            LEFT JOIN FETCH ia.alimento
            WHERE i.alerta.id = :alertaId
            ORDER BY i.id""")
    List<ImpactoRegion> buscarPorAlerta(@Param("alertaId") Long alertaId);

    @Query("""
            SELECT DISTINCT i FROM ImpactoRegion i
            JOIN FETCH i.alerta al
            JOIN FETCH i.departamento d
            LEFT JOIN FETCH i.alimentos ia
            LEFT JOIN FETCH ia.alimento
            WHERE al.vigente = true AND UPPER(d.codigo) = UPPER(:codigo)
            ORDER BY i.id""")
    List<ImpactoRegion> buscarVigentesPorDepartamento(@Param("codigo") String codigo);

    @Query("""
            SELECT DISTINCT i FROM ImpactoRegion i
            JOIN FETCH i.alerta JOIN FETCH i.departamento
            LEFT JOIN FETCH i.alimentos ia LEFT JOIN FETCH ia.alimento
            WHERE i.id = :id""")
    Optional<ImpactoRegion> buscarConDetalle(@Param("id") Long id);
}
