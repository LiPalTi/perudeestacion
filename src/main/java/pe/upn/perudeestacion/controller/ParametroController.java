package pe.upn.perudeestacion.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.upn.perudeestacion.dto.ParametroRequest;
import pe.upn.perudeestacion.dto.ParametroResponse;
import pe.upn.perudeestacion.service.ParametroService;

/** Parametros del simulador. Solo ADMIN (lectura y escritura). */
@RestController
@RequestMapping("/api/v1/parametros-simulador")
public class ParametroController {

    private final ParametroService service;

    public ParametroController(ParametroService service) {
        this.service = service;
    }

    @GetMapping
    public List<ParametroResponse> listar() {
        return service.listar();
    }

    @PutMapping("/{clave}")
    public ParametroResponse actualizar(@PathVariable String clave, @Valid @RequestBody ParametroRequest req) {
        return service.actualizar(clave, req.valor());
    }
}
