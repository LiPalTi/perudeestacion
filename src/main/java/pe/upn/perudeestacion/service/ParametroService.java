package pe.upn.perudeestacion.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.perudeestacion.dto.ParametroResponse;
import pe.upn.perudeestacion.entity.ParametroSimulador;
import pe.upn.perudeestacion.exception.RecursoNoEncontradoException;
import pe.upn.perudeestacion.repository.ParametroSimuladorRepository;

@Service
@Transactional
public class ParametroService {

    private final ParametroSimuladorRepository parametroRepository;

    public ParametroService(ParametroSimuladorRepository parametroRepository) {
        this.parametroRepository = parametroRepository;
    }

    @Transactional(readOnly = true)
    public List<ParametroResponse> listar() {
        return parametroRepository.findAll().stream().map(ParametroResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> mapa() {
        return parametroRepository.findAll().stream()
                .collect(Collectors.toMap(ParametroSimulador::getClave, ParametroSimulador::getValor));
    }

    public ParametroResponse actualizar(String clave, BigDecimal valor) {
        ParametroSimulador p = parametroRepository.findById(clave)
                .orElseThrow(() -> new RecursoNoEncontradoException("Parametro", clave));
        p.setValor(valor);
        return ParametroResponse.from(parametroRepository.save(p));
    }
}
