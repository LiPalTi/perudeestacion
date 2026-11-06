package pe.upn.perudeestacion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.perudeestacion.dto.AlertaRequest;
import pe.upn.perudeestacion.dto.AlertaResponse;
import pe.upn.perudeestacion.dto.ImpactoResponse;
import pe.upn.perudeestacion.entity.AlertaNino;
import pe.upn.perudeestacion.exception.ConflictoException;
import pe.upn.perudeestacion.exception.RecursoNoEncontradoException;
import pe.upn.perudeestacion.repository.AlertaNinoRepository;
import pe.upn.perudeestacion.repository.ImpactoRegionRepository;

@Service
@Transactional
public class AlertaService {

    private final AlertaNinoRepository alertaRepository;
    private final ImpactoRegionRepository impactoRepository;

    public AlertaService(AlertaNinoRepository alertaRepository, ImpactoRegionRepository impactoRepository) {
        this.alertaRepository = alertaRepository;
        this.impactoRepository = impactoRepository;
    }

    @Transactional(readOnly = true)
    public List<AlertaResponse> listar(Boolean vigente) {
        List<AlertaNino> lista = vigente == null ? alertaRepository.findAllByOrderByFechaDesc()
                : alertaRepository.findByVigenteOrderByFechaDesc(vigente);
        return lista.stream().map(AlertaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AlertaResponse obtener(Long id) {
        return AlertaResponse.from(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public List<ImpactoResponse> impactos(Long alertaId) {
        buscarEntidad(alertaId);
        return impactoRepository.buscarPorAlerta(alertaId).stream().map(ImpactoResponse::from).toList();
    }

    /** Regla RN02: solo puede existir una alerta vigente a la vez. */
    public AlertaResponse crear(AlertaRequest req) {
        if (Boolean.TRUE.equals(req.vigente()) && alertaRepository.existsByVigenteTrue()) {
            throw new ConflictoException("Ya existe una alerta vigente. Cierre la anterior (vigente=false) primero");
        }
        AlertaNino a = new AlertaNino();
        copiar(req, a);
        return AlertaResponse.from(alertaRepository.save(a));
    }

    public AlertaResponse actualizar(Long id, AlertaRequest req) {
        AlertaNino a = buscarEntidad(id);
        if (Boolean.TRUE.equals(req.vigente()) && alertaRepository.existsByVigenteTrueAndIdNot(id)) {
            throw new ConflictoException("Ya existe otra alerta vigente. Cierre la anterior primero");
        }
        copiar(req, a);
        return AlertaResponse.from(alertaRepository.save(a));
    }

    public void eliminar(Long id) {
        alertaRepository.delete(buscarEntidad(id));
    }

    public AlertaNino buscarEntidad(Long id) {
        return alertaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Alerta", id));
    }

    private void copiar(AlertaRequest req, AlertaNino a) {
        a.setComunicado(req.comunicado().trim());
        a.setFecha(req.fecha());
        a.setEstado(req.estado().trim());
        a.setMagnitud(req.magnitud().trim());
        a.setVigente(req.vigente());
    }
}
