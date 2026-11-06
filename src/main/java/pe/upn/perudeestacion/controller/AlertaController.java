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
import pe.upn.perudeestacion.dto.AlertaRequest;
import pe.upn.perudeestacion.dto.AlertaResponse;
import pe.upn.perudeestacion.dto.ImpactoResponse;
import pe.upn.perudeestacion.service.AlertaService;

@RestController
@RequestMapping("/api/v1/alertas")
public class AlertaController {

    private final AlertaService service;

    public AlertaController(AlertaService service) {
        this.service = service;
    }

    /** RF08: alertas de El Nino (filtro opcional ?vigente=true). Publico. */
    @GetMapping
    public List<AlertaResponse> listar(@RequestParam(required = false) Boolean vigente) {
        return service.listar(vigente);
    }

    @GetMapping("/{id}")
    public AlertaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    /** RF08, RF09: impactos de la alerta por departamento, sector y alimento. Publico. */
    @GetMapping("/{id}/impactos")
    public List<ImpactoResponse> impactos(@PathVariable Long id) {
        return service.impactos(id);
    }

    @PostMapping
    public ResponseEntity<AlertaResponse> crear(@Valid @RequestBody AlertaRequest req) {
        AlertaResponse creada = service.crear(req);
        return ResponseEntity.created(URI.create("/api/v1/alertas/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    public AlertaResponse actualizar(@PathVariable Long id, @Valid @RequestBody AlertaRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
