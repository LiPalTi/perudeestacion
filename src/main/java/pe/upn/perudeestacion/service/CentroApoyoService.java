package pe.upn.perudeestacion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.perudeestacion.dto.CentroRequest;
import pe.upn.perudeestacion.dto.CentroResponse;
import pe.upn.perudeestacion.entity.CentroApoyo;
import pe.upn.perudeestacion.exception.RecursoNoEncontradoException;
import pe.upn.perudeestacion.repository.CentroApoyoRepository;

@Service
@Transactional
public class CentroApoyoService {

    private final CentroApoyoRepository centroRepository;
    private final ProgramaApoyoService programaService;
    private final DepartamentoService departamentoService;

    public CentroApoyoService(CentroApoyoRepository centroRepository, ProgramaApoyoService programaService,
            DepartamentoService departamentoService) {
        this.centroRepository = centroRepository;
        this.programaService = programaService;
        this.departamentoService = departamentoService;
    }

    @Transactional(readOnly = true)
    public List<CentroResponse> listar(String departamento, Long programaId) {
        boolean hayDep = departamento != null && !departamento.isBlank();
        List<CentroApoyo> lista;
        if (hayDep && programaId != null) {
            lista = centroRepository.findByDepartamentoCodigoIgnoreCaseAndProgramaIdOrderByNombreAsc(departamento,
                    programaId);
        } else if (hayDep) {
            lista = centroRepository.findByDepartamentoCodigoIgnoreCaseOrderByNombreAsc(departamento);
        } else if (programaId != null) {
            lista = centroRepository.findByProgramaIdOrderByNombreAsc(programaId);
        } else {
            lista = centroRepository.findAllByOrderByNombreAsc();
        }
        return lista.stream().map(CentroResponse::from).toList();
    }

    public CentroResponse crear(CentroRequest req) {
        CentroApoyo c = new CentroApoyo();
        copiar(req, c);
        return CentroResponse.from(centroRepository.save(c));
    }

    public CentroResponse actualizar(Long id, CentroRequest req) {
        CentroApoyo c = buscarEntidad(id);
        copiar(req, c);
        return CentroResponse.from(centroRepository.save(c));
    }

    public void eliminar(Long id) {
        centroRepository.delete(buscarEntidad(id));
    }

    private CentroApoyo buscarEntidad(Long id) {
        return centroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Centro de apoyo", id));
    }

    private void copiar(CentroRequest req, CentroApoyo c) {
        c.setPrograma(programaService.buscarEntidad(req.programaId()));
        c.setDepartamento(departamentoService.buscarEntidadPorId(req.departamentoId()));
        c.setNombre(req.nombre().trim());
        c.setDireccion(req.direccion().trim());
        c.setDistrito(req.distrito().trim());
        c.setTelefono(req.telefono());
    }
}
