package pe.upn.perudeestacion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.perudeestacion.dto.AlimentoFichaResponse;
import pe.upn.perudeestacion.dto.AlimentoRequest;
import pe.upn.perudeestacion.dto.AlimentoResponse;
import pe.upn.perudeestacion.dto.CalendarioResponse;
import pe.upn.perudeestacion.dto.ConsejoResponse;
import pe.upn.perudeestacion.dto.PrecioResponse;
import pe.upn.perudeestacion.entity.Alimento;
import pe.upn.perudeestacion.entity.Categoria;
import pe.upn.perudeestacion.entity.Departamento;
import pe.upn.perudeestacion.entity.NivelTemporada;
import pe.upn.perudeestacion.entity.Temporada;
import pe.upn.perudeestacion.exception.ConflictoException;
import pe.upn.perudeestacion.exception.RecursoNoEncontradoException;
import pe.upn.perudeestacion.repository.AlimentoRepository;
import pe.upn.perudeestacion.repository.ConsejoConservacionRepository;
import pe.upn.perudeestacion.repository.PrecioReferenciaRepository;
import pe.upn.perudeestacion.repository.TemporadaRepository;

@Service
@Transactional
public class AlimentoService {

    private final AlimentoRepository alimentoRepository;
    private final ConsejoConservacionRepository consejoRepository;
    private final PrecioReferenciaRepository precioRepository;
    private final TemporadaRepository temporadaRepository;
    private final DepartamentoService departamentoService;

    public AlimentoService(AlimentoRepository alimentoRepository, ConsejoConservacionRepository consejoRepository,
            PrecioReferenciaRepository precioRepository, TemporadaRepository temporadaRepository,
            DepartamentoService departamentoService) {
        this.alimentoRepository = alimentoRepository;
        this.consejoRepository = consejoRepository;
        this.precioRepository = precioRepository;
        this.temporadaRepository = temporadaRepository;
        this.departamentoService = departamentoService;
    }

    @Transactional(readOnly = true)
    public List<AlimentoResponse> listar(Categoria categoria, String q) {
        boolean hayTexto = q != null && !q.isBlank();
        List<Alimento> lista;
        if (categoria != null && hayTexto) {
            lista = alimentoRepository.findByCategoriaAndNombreContainingIgnoreCaseOrderByNombreAsc(categoria, q.trim());
        } else if (categoria != null) {
            lista = alimentoRepository.findByCategoriaOrderByNombreAsc(categoria);
        } else if (hayTexto) {
            lista = alimentoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(q.trim());
        } else {
            lista = alimentoRepository.findAllByOrderByNombreAsc();
        }
        return lista.stream().map(AlimentoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AlimentoFichaResponse ficha(Long id) {
        Alimento a = buscarEntidad(id);
        List<ConsejoResponse> consejos = consejoRepository.findByAlimentoIdOrderByIdAsc(id).stream()
                .map(ConsejoResponse::from).toList();
        List<PrecioResponse> precios = precioRepository.findTop5ByAlimentoIdOrderByFechaDesc(id).stream()
                .map(PrecioResponse::from).toList();
        return new AlimentoFichaResponse(AlimentoResponse.from(a), consejos, precios);
    }

    @Transactional(readOnly = true)
    public CalendarioResponse calendario(Long id, String codigoDepartamento) {
        Alimento a = buscarEntidad(id);
        Departamento d = departamentoService.buscarEntidadPorCodigo(codigoDepartamento);
        NivelTemporada[] niveles = new NivelTemporada[13];
        for (Temporada t : temporadaRepository.calendario(id, codigoDepartamento)) {
            niveles[t.getMes()] = t.getNivel();
        }
        List<CalendarioResponse.MesNivel> meses = new java.util.ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            meses.add(new CalendarioResponse.MesNivel(m, niveles[m]));
        }
        return new CalendarioResponse(a.getId(), a.getNombre(), d.getCodigo(), meses);
    }

    public AlimentoResponse crear(AlimentoRequest req) {
        if (alimentoRepository.existsByNombreIgnoreCase(req.nombre().trim())) {
            throw new ConflictoException("Ya existe un alimento llamado '" + req.nombre().trim() + "'");
        }
        Alimento a = new Alimento();
        copiar(req, a);
        return AlimentoResponse.from(alimentoRepository.save(a));
    }

    public AlimentoResponse actualizar(Long id, AlimentoRequest req) {
        Alimento a = buscarEntidad(id);
        if (alimentoRepository.existsByNombreIgnoreCaseAndIdNot(req.nombre().trim(), id)) {
            throw new ConflictoException("Ya existe otro alimento llamado '" + req.nombre().trim() + "'");
        }
        copiar(req, a);
        return AlimentoResponse.from(alimentoRepository.save(a));
    }

    public void eliminar(Long id) {
        Alimento a = buscarEntidad(id);
        // Si el alimento tiene temporadas o precios, la FK de la BD lo impide y se responde 409.
        alimentoRepository.delete(a);
        alimentoRepository.flush();
    }

    public Alimento buscarEntidad(Long id) {
        return alimentoRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Alimento", id));
    }

    private void copiar(AlimentoRequest req, Alimento a) {
        a.setNombre(req.nombre().trim());
        a.setCategoria(req.categoria());
        a.setDescripcion(req.descripcion());
        a.setImagenUrl(req.imagenUrl());
    }
}
