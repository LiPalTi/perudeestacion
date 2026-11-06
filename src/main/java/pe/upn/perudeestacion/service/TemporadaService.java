package pe.upn.perudeestacion.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.perudeestacion.dto.AlertaResponse;
import pe.upn.perudeestacion.dto.DepartamentoResponse;
import pe.upn.perudeestacion.dto.TemporadaConsultaResponse;
import pe.upn.perudeestacion.dto.TemporadaRequest;
import pe.upn.perudeestacion.dto.TemporadaResponse;
import pe.upn.perudeestacion.entity.Categoria;
import pe.upn.perudeestacion.entity.Departamento;
import pe.upn.perudeestacion.entity.ImpactoAlimento;
import pe.upn.perudeestacion.entity.ImpactoRegion;
import pe.upn.perudeestacion.entity.NivelRiesgo;
import pe.upn.perudeestacion.entity.PrecioReferencia;
import pe.upn.perudeestacion.entity.Temporada;
import pe.upn.perudeestacion.exception.ConflictoException;
import pe.upn.perudeestacion.exception.RecursoNoEncontradoException;
import pe.upn.perudeestacion.repository.ImpactoRegionRepository;
import pe.upn.perudeestacion.repository.PrecioConsultaRepository;
import pe.upn.perudeestacion.repository.TemporadaRepository;

@Service
@Transactional
public class TemporadaService {

    private final TemporadaRepository temporadaRepository;
    private final PrecioConsultaRepository precioConsultaRepository;
    private final ImpactoRegionRepository impactoRepository;
    private final AlimentoService alimentoService;
    private final DepartamentoService departamentoService;

    public TemporadaService(TemporadaRepository temporadaRepository, PrecioConsultaRepository precioConsultaRepository,
            ImpactoRegionRepository impactoRepository, AlimentoService alimentoService,
            DepartamentoService departamentoService) {
        this.temporadaRepository = temporadaRepository;
        this.precioConsultaRepository = precioConsultaRepository;
        this.impactoRepository = impactoRepository;
        this.alimentoService = alimentoService;
        this.departamentoService = departamentoService;
    }

    /**
     * Consulta principal (RF02, RF03, RF09): alimentos en temporada de un departamento y mes,
     * ordenados de ALTA a BAJA, con su ultimo precio y el riesgo de alza si hay alerta El Nino vigente.
     */
    @Transactional(readOnly = true)
    public TemporadaConsultaResponse consultar(String codigo, int mes, Categoria categoria) {
        Departamento d = departamentoService.buscarEntidadPorCodigo(codigo);
        Map<Long, PrecioReferencia> precios = precioConsultaRepository.ultimoPrecioPorAlimento(codigo);

        List<ImpactoRegion> impactos = impactoRepository.buscarVigentesPorDepartamento(codigo);
        Map<Long, NivelRiesgo> riesgo = impactos.stream()
                .flatMap(i -> i.getAlimentos().stream())
                .collect(Collectors.toMap(ia -> ia.getAlimento().getId(), ImpactoAlimento::getRiesgoAlza,
                        (r1, r2) -> r1.ordinal() <= r2.ordinal() ? r1 : r2));
        AlertaResponse alerta = impactos.isEmpty() ? null : AlertaResponse.from(impactos.get(0).getAlerta());

        List<TemporadaConsultaResponse.Item> items = temporadaRepository.buscarPorDepartamentoYMes(codigo, mes).stream()
                .filter(t -> categoria == null || t.getAlimento().getCategoria() == categoria)
                .sorted(Comparator.comparing(Temporada::getNivel).thenComparing(t -> t.getAlimento().getNombre()))
                .map(t -> {
                    Long alimentoId = t.getAlimento().getId();
                    PrecioReferencia p = precios.get(alimentoId);
                    return new TemporadaConsultaResponse.Item(alimentoId, t.getAlimento().getNombre(),
                            t.getAlimento().getCategoria(), t.getNivel(), riesgo.get(alimentoId),
                            p == null ? null : p.getPrecioKg(), p == null ? null : p.getFuente(),
                            p == null ? null : p.getFecha());
                })
                .toList();
        return new TemporadaConsultaResponse(DepartamentoResponse.from(d), mes, alerta, items);
    }

    @Transactional(readOnly = true)
    public TemporadaResponse obtener(Long id) {
        return TemporadaResponse.from(buscarEntidad(id));
    }

    public TemporadaResponse crear(TemporadaRequest req) {
        if (temporadaRepository.existsByAlimentoIdAndDepartamentoIdAndMes(req.alimentoId(), req.departamentoId(),
                req.mes())) {
            throw new ConflictoException("Ya existe una temporada para ese alimento, departamento y mes");
        }
        Temporada t = new Temporada();
        copiar(req, t);
        return TemporadaResponse.from(temporadaRepository.save(t));
    }

    public TemporadaResponse actualizar(Long id, TemporadaRequest req) {
        Temporada t = buscarEntidad(id);
        if (temporadaRepository.existsByAlimentoIdAndDepartamentoIdAndMesAndIdNot(req.alimentoId(),
                req.departamentoId(), req.mes(), id)) {
            throw new ConflictoException("Ya existe otra temporada para ese alimento, departamento y mes");
        }
        copiar(req, t);
        return TemporadaResponse.from(temporadaRepository.save(t));
    }

    public void eliminar(Long id) {
        temporadaRepository.delete(buscarEntidad(id));
    }

    private Temporada buscarEntidad(Long id) {
        return temporadaRepository.buscarConDetalle(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Temporada", id));
    }

    private void copiar(TemporadaRequest req, Temporada t) {
        t.setAlimento(alimentoService.buscarEntidad(req.alimentoId()));
        t.setDepartamento(departamentoService.buscarEntidadPorId(req.departamentoId()));
        t.setMes(req.mes());
        t.setNivel(req.nivel());
    }
}
