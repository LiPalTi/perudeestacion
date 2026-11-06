package pe.upn.perudeestacion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.perudeestacion.dto.AlertaResponse;
import pe.upn.perudeestacion.dto.DepartamentoDetalleResponse;
import pe.upn.perudeestacion.dto.DepartamentoResponse;
import pe.upn.perudeestacion.dto.ImpactoResponse;
import pe.upn.perudeestacion.entity.Departamento;
import pe.upn.perudeestacion.entity.ImpactoRegion;
import pe.upn.perudeestacion.exception.RecursoNoEncontradoException;
import pe.upn.perudeestacion.repository.DepartamentoRepository;
import pe.upn.perudeestacion.repository.ImpactoRegionRepository;

@Service
@Transactional(readOnly = true)
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final ImpactoRegionRepository impactoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository,
            ImpactoRegionRepository impactoRepository) {
        this.departamentoRepository = departamentoRepository;
        this.impactoRepository = impactoRepository;
    }

    public List<DepartamentoResponse> listar() {
        return departamentoRepository.findAllByOrderByNombreAsc().stream().map(DepartamentoResponse::from).toList();
    }

    public DepartamentoDetalleResponse detalle(String codigo) {
        Departamento d = buscarEntidadPorCodigo(codigo);
        List<ImpactoRegion> vigentes = impactoRepository.buscarVigentesPorDepartamento(codigo);
        AlertaResponse alerta = vigentes.isEmpty() ? null : AlertaResponse.from(vigentes.get(0).getAlerta());
        List<ImpactoResponse> impactos = vigentes.stream().map(ImpactoResponse::from).toList();
        return new DepartamentoDetalleResponse(DepartamentoResponse.from(d), alerta, impactos);
    }

    public Departamento buscarEntidadPorCodigo(String codigo) {
        return departamentoRepository.findByCodigoIgnoreCase(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento", codigo));
    }

    public Departamento buscarEntidadPorId(Long id) {
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento", id));
    }
}
