package pe.upn.perudeestacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.upn.perudeestacion.entity.ConsejoConservacion;

public interface ConsejoConservacionRepository extends JpaRepository<ConsejoConservacion, Long> {

    List<ConsejoConservacion> findByAlimentoIdOrderByIdAsc(Long alimentoId);
}
