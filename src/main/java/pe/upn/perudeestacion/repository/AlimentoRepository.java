package pe.upn.perudeestacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.upn.perudeestacion.entity.Alimento;
import pe.upn.perudeestacion.entity.Categoria;

public interface AlimentoRepository extends JpaRepository<Alimento, Long> {

    List<Alimento> findAllByOrderByNombreAsc();

    List<Alimento> findByCategoriaOrderByNombreAsc(Categoria categoria);

    List<Alimento> findByNombreContainingIgnoreCaseOrderByNombreAsc(String texto);

    List<Alimento> findByCategoriaAndNombreContainingIgnoreCaseOrderByNombreAsc(Categoria categoria, String texto);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
