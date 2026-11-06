package pe.upn.perudeestacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.upn.perudeestacion.entity.AlertaNino;

public interface AlertaNinoRepository extends JpaRepository<AlertaNino, Long> {

    Optional<AlertaNino> findFirstByVigenteTrue();

    List<AlertaNino> findAllByOrderByFechaDesc();

    List<AlertaNino> findByVigenteOrderByFechaDesc(Boolean vigente);

    boolean existsByVigenteTrue();

    boolean existsByVigenteTrueAndIdNot(Long id);
}
