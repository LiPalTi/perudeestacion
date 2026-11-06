package pe.upn.perudeestacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.upn.perudeestacion.entity.ParametroSimulador;

public interface ParametroSimuladorRepository extends JpaRepository<ParametroSimulador, String> {
}
