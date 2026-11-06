package pe.upn.perudeestacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.upn.perudeestacion.entity.ProgramaApoyo;

public interface ProgramaApoyoRepository extends JpaRepository<ProgramaApoyo, Long> {

    @Query("SELECT DISTINCT p FROM ProgramaApoyo p LEFT JOIN FETCH p.requisitos ORDER BY p.nombre")
    List<ProgramaApoyo> listarConRequisitos();

    @Query("SELECT p FROM ProgramaApoyo p LEFT JOIN FETCH p.requisitos WHERE p.id = :id")
    Optional<ProgramaApoyo> buscarConRequisitos(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
