package pe.upn.perudeestacion.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.upn.perudeestacion.dto.DepartamentoDetalleResponse;
import pe.upn.perudeestacion.dto.DepartamentoResponse;
import pe.upn.perudeestacion.service.DepartamentoService;

@RestController
@RequestMapping("/api/v1/departamentos")
public class DepartamentoController {

    private final DepartamentoService service;

    public DepartamentoController(DepartamentoService service) {
        this.service = service;
    }

    /** RF01: lista los 24 departamentos y el Callao. */
    @GetMapping
    public List<DepartamentoResponse> listar() {
        return service.listar();
    }

    /** Detalle por codigo (PIU, LIM...) con la alerta El Nino vigente y sus impactos. */
    @GetMapping("/{codigo}")
    public DepartamentoDetalleResponse detalle(@PathVariable String codigo) {
        return service.detalle(codigo);
    }
}
