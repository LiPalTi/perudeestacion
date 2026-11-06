package pe.upn.perudeestacion.repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pe.upn.perudeestacion.entity.PrecioReferencia;

/**
 * Acceso a datos con EntityManager y @Repository (saber de la semana 2 del silabo).
 * Obtiene, en una sola consulta, el precio mas reciente de cada alimento en un departamento.
 */
@Repository
public class PrecioConsultaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Map<Long, PrecioReferencia> ultimoPrecioPorAlimento(String codigoDepartamento) {
        List<PrecioReferencia> precios = entityManager.createQuery("""
                SELECT p FROM PrecioReferencia p
                JOIN FETCH p.alimento a
                JOIN FETCH p.departamento d
                WHERE UPPER(d.codigo) = UPPER(:codigo)
                  AND p.fecha = (SELECT MAX(p2.fecha) FROM PrecioReferencia p2
                                 WHERE p2.alimento = p.alimento AND p2.departamento = p.departamento)
                """, PrecioReferencia.class)
                .setParameter("codigo", codigoDepartamento)
                .getResultList();
        return precios.stream()
                .collect(Collectors.toMap(p -> p.getAlimento().getId(), Function.identity(), (a, b) -> a));
    }
}
