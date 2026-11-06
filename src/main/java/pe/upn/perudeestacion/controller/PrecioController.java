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
import pe.upn.perudeestacion.dto.PrecioRequest;
import pe.upn.perudeestacion.dto.PrecioResponse;
import pe.upn.perudeestacion.service.PrecioService;

@RestController
@RequestMapping("/api/v1/precios")
public class PrecioController {

    private final PrecioService service;

    public PrecioController(PrecioService service) {
        this.service = service;
    }

    @GetMapping
    public List<PrecioResponse> listar(@RequestParam(required = false) Long alimento,
            @RequestParam(required = false) String departamento) {
        return service.listar(alimento, departamento);
    }

    @PostMapping
    public ResponseEntity<PrecioResponse> crear(@Valid @RequestBody PrecioRequest req) {
        PrecioResponse creado = service.crear(req);
        return ResponseEntity.created(URI.create("/api/v1/precios/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public PrecioResponse actualizar(@PathVariable Long id, @Valid @RequestBody PrecioRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
