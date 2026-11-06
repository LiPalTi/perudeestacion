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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.upn.perudeestacion.dto.ProgramaRequest;
import pe.upn.perudeestacion.dto.ProgramaResponse;
import pe.upn.perudeestacion.service.ProgramaApoyoService;

@RestController
@RequestMapping("/api/v1/programas-apoyo")
public class ProgramaApoyoController {

    private final ProgramaApoyoService service;

    public ProgramaApoyoController(ProgramaApoyoService service) {
        this.service = service;
    }

    /** RF11: programas de apoyo con sus requisitos. Publico. */
    @GetMapping
    public List<ProgramaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ProgramaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<ProgramaResponse> crear(@Valid @RequestBody ProgramaRequest req) {
        ProgramaResponse creado = service.crear(req);
        return ResponseEntity.created(URI.create("/api/v1/programas-apoyo/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public ProgramaResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProgramaRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
