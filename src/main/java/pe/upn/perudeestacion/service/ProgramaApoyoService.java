package pe.upn.perudeestacion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.perudeestacion.dto.ProgramaRequest;
import pe.upn.perudeestacion.dto.ProgramaResponse;
import pe.upn.perudeestacion.entity.ProgramaApoyo;
import pe.upn.perudeestacion.entity.Requisito;
import pe.upn.perudeestacion.exception.ConflictoException;
import pe.upn.perudeestacion.exception.RecursoNoEncontradoException;
import pe.upn.perudeestacion.repository.ProgramaApoyoRepository;

@Service
@Transactional
public class ProgramaApoyoService {

    private final ProgramaApoyoRepository programaRepository;

    public ProgramaApoyoService(ProgramaApoyoRepository programaRepository) {
        this.programaRepository = programaRepository;
    }

    @Transactional(readOnly = true)
    public List<ProgramaResponse> listar() {
        return programaRepository.listarConRequisitos().stream().map(ProgramaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProgramaResponse obtener(Long id) {
        return ProgramaResponse.from(buscarEntidad(id));
    }

    public ProgramaResponse crear(ProgramaRequest req) {
        if (programaRepository.existsByNombreIgnoreCase(req.nombre().trim())) {
            throw new ConflictoException("Ya existe un programa llamado '" + req.nombre().trim() + "'");
        }
        ProgramaApoyo p = new ProgramaApoyo();
        copiar(req, p);
        return ProgramaResponse.from(programaRepository.save(p));
    }

    public ProgramaResponse actualizar(Long id, ProgramaRequest req) {
        ProgramaApoyo p = buscarEntidad(id);
        if (programaRepository.existsByNombreIgnoreCaseAndIdNot(req.nombre().trim(), id)) {
            throw new ConflictoException("Ya existe otro programa llamado '" + req.nombre().trim() + "'");
        }
        copiar(req, p);
        return ProgramaResponse.from(programaRepository.save(p));
    }

    public void eliminar(Long id) {
        programaRepository.delete(buscarEntidad(id));
        programaRepository.flush(); // si tiene centros asociados, la FK responde 409
    }

    public ProgramaApoyo buscarEntidad(Long id) {
        return programaRepository.buscarConRequisitos(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Programa de apoyo", id));
    }

    private void copiar(ProgramaRequest req, ProgramaApoyo p) {
        p.setNombre(req.nombre().trim());
        p.setEntidad(req.entidad().trim());
        p.setUrlOficial(req.urlOficial().trim());
        p.setFechaRevision(req.fechaRevision());
        p.getRequisitos().clear();
        int orden = 1;
        for (String texto : req.requisitos()) {
            Requisito r = new Requisito();
            r.setPrograma(p);
            r.setDescripcion(texto.trim());
            r.setOrden(orden++);
            p.getRequisitos().add(r);
        }
    }
}
