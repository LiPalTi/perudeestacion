package pe.upn.perudeestacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.upn.perudeestacion.entity.Temporada;

public interface TemporadaRepository extends JpaRepository<Temporada, Long> {

    /** Usa la @NamedQuery "Temporada.buscarPorDepartamentoYMes" declarada en la entidad. */
    List<Temporada> buscarPorDepartamentoYMes(@Param("codigo") String codigo, @Param("mes") Integer mes);

    /** JPQL con parametros nombrados: los valores viajan enlazados, nunca concatenados (evita inyeccion SQL). */
    @Query("""
            SELECT t FROM Temporada t
            JOIN FETCH t.alimento a
            JOIN FETCH t.departamento d
            WHERE a.id = :alimentoId AND UPPER(d.codigo) = UPPER(:codigo)
            ORDER BY t.mes""")
    List<Temporada> calendario(@Param("alimentoId") Long alimentoId, @Param("codigo") String codigo);

    @Query("SELECT t FROM Temporada t JOIN FETCH t.alimento JOIN FETCH t.departamento WHERE t.id = :id")
    java.util.Optional<Temporada> buscarConDetalle(@Param("id") Long id);

    boolean existsByAlimentoIdAndDepartamentoIdAndMes(Long alimentoId, Long departamentoId, Integer mes);

    boolean existsByAlimentoIdAndDepartamentoIdAndMesAndIdNot(Long alimentoId, Long departamentoId, Integer mes,
            Long id);
}
