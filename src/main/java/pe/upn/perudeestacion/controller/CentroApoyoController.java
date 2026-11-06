package pe.upn.perudeestacion.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.upn.perudeestacion.dto.CentroRequest;
import pe.upn.perudeestacion.dto.CentroResponse;
import pe.upn.perudeestacion.service.CentroApoyoService;

@RestController
@RequestMapping("/api/v1/centros-apoyo")
public class CentroApoyoController {

    private final CentroApoyoService service;

    public CentroApoyoController(CentroApoyoService service) {
        this.service = service;
    }

    /** RF11: centros de atencion filtrados por departamento y/o programa. Publico. */
    @GetMapping
    public List<CentroResponse> listar(@RequestParam(required = false) String departamento,
            @RequestParam(required = false) Long programa) {
        return service.listar(departamento, programa);
    }

    @PostMapping
    public ResponseEntity<CentroResponse> crear(@Valid @RequestBody CentroRequest req) {
        CentroResponse creado = service.crear(req);
        return ResponseEntity.created(URI.create("/api/v1/centros-apoyo/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public CentroResponse actualizar(@PathVariable Long id, @Valid @RequestBody CentroRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
