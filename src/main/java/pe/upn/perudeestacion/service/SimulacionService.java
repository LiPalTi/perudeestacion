package pe.upn.perudeestacion.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.perudeestacion.dto.RacionRequest;
import pe.upn.perudeestacion.dto.RacionResponse;
import pe.upn.perudeestacion.entity.CapacidadEconomica;
import pe.upn.perudeestacion.entity.Departamento;
import pe.upn.perudeestacion.entity.ProgramaApoyo;
import pe.upn.perudeestacion.entity.Simulacion;
import pe.upn.perudeestacion.entity.TipoVivienda;
import pe.upn.perudeestacion.repository.CentroApoyoRepository;
import pe.upn.perudeestacion.repository.ImpactoRegionRepository;
import pe.upn.perudeestacion.repository.ProgramaApoyoRepository;
import pe.upn.perudeestacion.repository.SimulacionRepository;

/**
 * Simulador de racion minima (RF10).
 * E_hogar = KCAL_PERSONA_DIA x (adultos + FACTOR_NINO x ninos + FACTOR_ADULTO_MAYOR x mayores) x dias
 * kg por grupo = E_hogar x P_grupo / KCAL_KG_grupo ; costo = kg x PRECIO_KG_grupo
 * Todos los valores salen de la tabla parametro_simulador, editable por el administrador.
 */
@Service
@Transactional
public class SimulacionService {

    /** Grupos de la canasta de emergencia; cada uno usa las claves P_, KCAL_KG_ y PRECIO_KG_ + codigo. */
    private static final String[][] GRUPOS = {
            {"CEREALES_TUBERCULOS", "Cereales y tuberculos"},
            {"MENESTRAS", "Menestras"},
            {"ACEITE", "Aceite vegetal"},
            {"CONSERVAS_PESCADO", "Conservas de pescado"},
            {"AZUCAR", "Azucar"}};

    private final ParametroService parametroService;
    private final DepartamentoService departamentoService;
    private final SimulacionRepository simulacionRepository;
    private final ImpactoRegionRepository impactoRepository;
    private final ProgramaApoyoRepository programaRepository;
    private final CentroApoyoRepository centroRepository;

    public SimulacionService(ParametroService parametroService, DepartamentoService departamentoService,
            SimulacionRepository simulacionRepository, ImpactoRegionRepository impactoRepository,
            ProgramaApoyoRepository programaRepository, CentroApoyoRepository centroRepository) {
        this.parametroService = parametroService;
        this.departamentoService = departamentoService;
        this.simulacionRepository = simulacionRepository;
        this.impactoRepository = impactoRepository;
        this.programaRepository = programaRepository;
        this.centroRepository = centroRepository;
    }

    public RacionResponse calcular(RacionRequest req) {
        Departamento d = departamentoService.buscarEntidadPorCodigo(req.departamento());
        Map<String, BigDecimal> p = parametroService.mapa();

        BigDecimal personasEquivalentes = BigDecimal.valueOf(req.adultos())
                .add(param(p, "FACTOR_NINO").multiply(BigDecimal.valueOf(req.ninos())))
                .add(param(p, "FACTOR_ADULTO_MAYOR").multiply(BigDecimal.valueOf(req.adultosMayores())));
        int kcalDiarias = param(p, "KCAL_PERSONA_DIA").multiply(personasEquivalentes)
                .setScale(0, RoundingMode.HALF_UP).intValue();
        int kcalTotal = kcalDiarias * req.dias();

        List<RacionResponse.Grupo> lista = new ArrayList<>();
        BigDecimal costoTotal = BigDecimal.ZERO;
        for (String[] g : GRUPOS) {
            BigDecimal kg = BigDecimal.valueOf(kcalTotal).multiply(param(p, "P_" + g[0]))
                    .divide(param(p, "KCAL_KG_" + g[0]), 2, RoundingMode.HALF_UP);
            BigDecimal costo = kg.multiply(param(p, "PRECIO_KG_" + g[0])).setScale(2, RoundingMode.HALF_UP);
            costoTotal = costoTotal.add(costo);
            lista.add(new RacionResponse.Grupo(g[1], kg, costo));
        }

        Simulacion s = new Simulacion();
        s.setDepartamento(d);
        s.setAdultos(req.adultos());
        s.setNinos(req.ninos());
        s.setAdultosMayores(req.adultosMayores());
        s.setDias(req.dias());
        s.setTipoVivienda(req.tipoVivienda());
        s.setCapacidadEconomica(req.capacidadEconomica());
        s.setKcalTotal(kcalTotal);
        s.setCostoEstimado(costoTotal);
        s.setCreadoEn(LocalDateTime.now());
        Long id = simulacionRepository.save(s).getId();

        boolean hayAlerta = !impactoRepository.buscarVigentesPorDepartamento(d.getCodigo()).isEmpty();
        return new RacionResponse(id, d.getCodigo(), kcalDiarias, kcalTotal, req.dias(), lista, costoTotal,
                recomendaciones(req.tipoVivienda(), hayAlerta, d.getNombre()),
                apoyo(req.capacidadEconomica(), d.getCodigo()));
    }

    private List<String> recomendaciones(TipoVivienda vivienda, boolean hayAlerta, String departamento) {
        List<String> r = new ArrayList<>();
        if (vivienda == TipoVivienda.MATERIAL_NOBLE) {
            r.add("Guarda la reserva en la parte mas alta de la vivienda, lejos de la humedad.");
        } else {
            r.add("Guarda la reserva en recipientes hermeticos y elevados del suelo: tu vivienda es vulnerable a inundaciones.");
            r.add("Prepara la mochila de emergencia y la caja de reserva recomendadas por el INDECI.");
        }
        r.add("Prioriza alimentos que no requieren coccion ni refrigeracion.");
        if (hayAlerta) {
            r.add("Hay una alerta de El Nino vigente en " + departamento
                    + ": revisa en /temporadas los alimentos con riesgo de alza antes de comprar.");
        }
        return r;
    }

    private List<RacionResponse.Apoyo> apoyo(CapacidadEconomica capacidad, String codigo) {
        if (capacidad != CapacidadEconomica.BAJA) {
            return List.of();
        }
        List<RacionResponse.Apoyo> lista = new ArrayList<>();
        for (ProgramaApoyo prog : programaRepository.listarConRequisitos()) {
            long centros = centroRepository.countByProgramaIdAndDepartamentoCodigoIgnoreCase(prog.getId(), codigo);
            lista.add(new RacionResponse.Apoyo(prog.getId(), prog.getNombre(), prog.getUrlOficial(), centros));
        }
        return lista;
    }

    private static BigDecimal param(Map<String, BigDecimal> p, String clave) {
        BigDecimal v = p.get(clave);
        if (v == null) {
            // Falta configurar un parametro en la BD: es un error del servidor (500), no del cliente.
            throw new IllegalStateException("Falta el parametro del simulador: " + clave);
        }
        return v;
    }
}
