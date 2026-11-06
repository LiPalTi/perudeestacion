package pe.upn.perudeestacion.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.upn.perudeestacion.dto.ImpactoRequest;
import pe.upn.perudeestacion.dto.ImpactoResponse;
import pe.upn.perudeestacion.service.ImpactoService;

@RestController
@RequestMapping("/api/v1/impactos")
public class ImpactoController {

    private final ImpactoService service;

    public ImpactoController(ImpactoService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ImpactoResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    /** RF12: registra el impacto de una alerta en un departamento y los alimentos con riesgo de alza. */
    @PostMapping
    public ResponseEntity<ImpactoResponse> crear(@Valid @RequestBody ImpactoRequest req) {
        ImpactoResponse creado = service.crear(req);
        return ResponseEntity.created(URI.create("/api/v1/impactos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public ImpactoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ImpactoRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
