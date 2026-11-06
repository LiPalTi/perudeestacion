package pe.upn.perudeestacion.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.perudeestacion.dto.ImpactoRequest;
import pe.upn.perudeestacion.dto.ImpactoResponse;
import pe.upn.perudeestacion.entity.ImpactoAlimento;
import pe.upn.perudeestacion.entity.ImpactoRegion;
import pe.upn.perudeestacion.exception.ConflictoException;
import pe.upn.perudeestacion.exception.RecursoNoEncontradoException;
import pe.upn.perudeestacion.repository.ImpactoRegionRepository;

@Service
@Transactional
public class ImpactoService {

    private final ImpactoRegionRepository impactoRepository;
    private final AlertaService alertaService;
    private final DepartamentoService departamentoService;
    private final AlimentoService alimentoService;

    public ImpactoService(ImpactoRegionRepository impactoRepository, AlertaService alertaService,
            DepartamentoService departamentoService, AlimentoService alimentoService) {
        this.impactoRepository = impactoRepository;
        this.alertaService = alertaService;
        this.departamentoService = departamentoService;
        this.alimentoService = alimentoService;
    }

    @Transactional(readOnly = true)
    public ImpactoResponse obtener(Long id) {
        return ImpactoResponse.from(buscarEntidad(id));
    }

    public ImpactoResponse crear(ImpactoRequest req) {
        ImpactoRegion i = new ImpactoRegion();
        copiar(req, i);
        return ImpactoResponse.from(impactoRepository.save(i));
    }

    public ImpactoResponse actualizar(Long id, ImpactoRequest req) {
        ImpactoRegion i = buscarEntidad(id);
        copiar(req, i);
        return ImpactoResponse.from(impactoRepository.save(i));
    }

    public void eliminar(Long id) {
        impactoRepository.delete(buscarEntidad(id));
    }

    private ImpactoRegion buscarEntidad(Long id) {
        return impactoRepository.buscarConDetalle(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Impacto", id));
    }

    private void copiar(ImpactoRequest req, ImpactoRegion i) {
        i.setAlerta(alertaService.buscarEntidad(req.alertaId()));
        i.setDepartamento(departamentoService.buscarEntidadPorId(req.departamentoId()));
        i.setSector(req.sector().trim());
        i.setNivelRiesgo(req.nivelRiesgo());
        i.setDescripcion(req.descripcion());

        List<ImpactoRequest.AlimentoAfectado> pedidos = req.alimentos() == null ? List.of() : req.alimentos();
        Set<Long> vistos = new HashSet<>();
        for (ImpactoRequest.AlimentoAfectado a : pedidos) {
            if (!vistos.add(a.alimentoId())) {
                throw new ConflictoException("El alimento " + a.alimentoId() + " esta repetido en la lista");
            }
        }
        // Se actualiza la lista en sitio (y no se borra y recrea) para no chocar con la restriccion
        // UNIQUE (impacto_id, alimento_id) durante el flush de Hibernate.
        Map<Long, ImpactoAlimento> actuales = i.getAlimentos().stream()
                .collect(Collectors.toMap(ia -> ia.getAlimento().getId(), Function.identity()));
        i.getAlimentos().removeIf(ia -> !vistos.contains(ia.getAlimento().getId()));
        for (ImpactoRequest.AlimentoAfectado a : pedidos) {
            ImpactoAlimento ia = actuales.get(a.alimentoId());
            if (ia == null) {
                ia = new ImpactoAlimento();
                ia.setImpacto(i);
                ia.setAlimento(alimentoService.buscarEntidad(a.alimentoId()));
                i.getAlimentos().add(ia);
            }
            ia.setRiesgoAlza(a.riesgoAlza());
        }
    }
}
