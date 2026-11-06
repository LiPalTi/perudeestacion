package pe.upn.perudeestacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.upn.perudeestacion.entity.Departamento;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    // Consultas derivadas: Spring Data genera el SQL a partir del nombre del metodo.
    Optional<Departamento> findByCodigoIgnoreCase(String codigo);

    List<Departamento> findAllByOrderByNombreAsc();
}
