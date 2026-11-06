package pe.upn.perudeestacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import pe.upn.perudeestacion.entity.CentroApoyo;

public interface CentroApoyoRepository extends JpaRepository<CentroApoyo, Long> {

    @EntityGraph(attributePaths = {"programa", "departamento"})
    List<CentroApoyo> findAllByOrderByNombreAsc();

    @EntityGraph(attributePaths = {"programa", "departamento"})
    List<CentroApoyo> findByDepartamentoCodigoIgnoreCaseOrderByNombreAsc(String codigo);

    @EntityGraph(attributePaths = {"programa", "departamento"})
    List<CentroApoyo> findByProgramaIdOrderByNombreAsc(Long programaId);

    @EntityGraph(attributePaths = {"programa", "departamento"})
    List<CentroApoyo> findByDepartamentoCodigoIgnoreCaseAndProgramaIdOrderByNombreAsc(String codigo, Long programaId);

    long countByProgramaIdAndDepartamentoCodigoIgnoreCase(Long programaId, String codigo);
}
