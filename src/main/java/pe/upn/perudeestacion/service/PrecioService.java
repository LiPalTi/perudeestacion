package pe.upn.perudeestacion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.perudeestacion.dto.PrecioRequest;
import pe.upn.perudeestacion.dto.PrecioResponse;
import pe.upn.perudeestacion.entity.PrecioReferencia;
import pe.upn.perudeestacion.exception.RecursoNoEncontradoException;
import pe.upn.perudeestacion.repository.PrecioReferenciaRepository;

@Service
@Transactional
public class PrecioService {

    private final PrecioReferenciaRepository precioRepository;
    private final AlimentoService alimentoService;
    private final DepartamentoService departamentoService;

    public PrecioService(PrecioReferenciaRepository precioRepository, AlimentoService alimentoService,
            DepartamentoService departamentoService) {
        this.precioRepository = precioRepository;
        this.alimentoService = alimentoService;
        this.departamentoService = departamentoService;
    }

    @Transactional(readOnly = true)
    public List<PrecioResponse> listar(Long alimentoId, String departamento) {
        List<PrecioReferencia> lista;
        if (alimentoId != null && departamento != null && !departamento.isBlank()) {
            lista = precioRepository.findByAlimentoIdAndDepartamentoCodigoIgnoreCaseOrderByFechaDesc(alimentoId,
                    departamento);
        } else if (alimentoId != null) {
            lista = precioRepository.findTop5ByAlimentoIdOrderByFechaDesc(alimentoId);
        } else {
            lista = precioRepository.findAllByOrderByFechaDesc();
        }
        return lista.stream().map(PrecioResponse::from).toList();
    }

    public PrecioResponse crear(PrecioRequest req) {
        PrecioReferencia p = new PrecioReferencia();
        copiar(req, p);
        return PrecioResponse.from(precioRepository.save(p));
    }

    public PrecioResponse actualizar(Long id, PrecioRequest req) {
        PrecioReferencia p = buscarEntidad(id);
        copiar(req, p);
        return PrecioResponse.from(precioRepository.save(p));
    }

    public void eliminar(Long id) {
        precioRepository.delete(buscarEntidad(id));
    }

    private PrecioReferencia buscarEntidad(Long id) {
        return precioRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Precio", id));
    }

    private void copiar(PrecioRequest req, PrecioReferencia p) {
        p.setAlimento(alimentoService.buscarEntidad(req.alimentoId()));
        p.setDepartamento(departamentoService.buscarEntidadPorId(req.departamentoId()));
        p.setPrecioKg(req.precioKg());
        p.setFecha(req.fecha());
        p.setFuente(req.fuente().trim());
    }
}
