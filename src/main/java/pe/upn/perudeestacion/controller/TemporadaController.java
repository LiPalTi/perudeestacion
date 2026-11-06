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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import pe.upn.perudeestacion.dto.TemporadaConsultaResponse;
import pe.upn.perudeestacion.dto.TemporadaRequest;
import pe.upn.perudeestacion.dto.TemporadaResponse;
import pe.upn.perudeestacion.entity.Categoria;
import pe.upn.perudeestacion.service.TemporadaService;

@RestController
@RequestMapping("/api/v1/temporadas")
public class TemporadaController {

    private final TemporadaService service;

    public TemporadaController(TemporadaService service) {
        this.service = service;
    }

    /** Endpoint principal (RF02, RF03, RF09). Ej: /api/v1/temporadas?departamento=PIU&mes=11&categoria=FRUTA */
    @GetMapping
    public TemporadaConsultaResponse consultar(@RequestParam String departamento,
            @RequestParam @Min(value = 1, message = "mes debe estar entre 1 y 12")
            @Max(value = 12, message = "mes debe estar entre 1 y 12") Integer mes,
            @RequestParam(required = false) Categoria categoria) {
        return service.consultar(departamento, mes, categoria);
    }

    @GetMapping("/{id}")
    public TemporadaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<TemporadaResponse> crear(@Valid @RequestBody TemporadaRequest req) {
        TemporadaResponse creada = service.crear(req);
        return ResponseEntity.created(URI.create("/api/v1/temporadas/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    public TemporadaResponse actualizar(@PathVariable Long id, @Valid @RequestBody TemporadaRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
