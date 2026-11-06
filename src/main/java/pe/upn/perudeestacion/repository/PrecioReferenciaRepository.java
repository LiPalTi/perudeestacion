package pe.upn.perudeestacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import pe.upn.perudeestacion.entity.PrecioReferencia;

public interface PrecioReferenciaRepository extends JpaRepository<PrecioReferencia, Long> {

    /** @EntityGraph carga alimento y departamento en la misma consulta (evita el problema N+1). */
    @EntityGraph(attributePaths = {"alimento", "departamento"})
    List<PrecioReferencia> findTop5ByAlimentoIdOrderByFechaDesc(Long alimentoId);

    @EntityGraph(attributePaths = {"alimento", "departamento"})
    List<PrecioReferencia> findByAlimentoIdAndDepartamentoCodigoIgnoreCaseOrderByFechaDesc(Long alimentoId,
            String codigo);

    @EntityGraph(attributePaths = {"alimento", "departamento"})
    List<PrecioReferencia> findAllByOrderByFechaDesc();
}
