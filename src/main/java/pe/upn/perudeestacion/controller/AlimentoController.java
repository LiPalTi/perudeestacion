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
import jakarta.validation.constraints.Size;
import pe.upn.perudeestacion.dto.AlimentoFichaResponse;
import pe.upn.perudeestacion.dto.AlimentoRequest;
import pe.upn.perudeestacion.dto.AlimentoResponse;
import pe.upn.perudeestacion.dto.CalendarioResponse;
import pe.upn.perudeestacion.entity.Categoria;
import pe.upn.perudeestacion.service.AlimentoService;

@RestController
@RequestMapping("/api/v1/alimentos")
public class AlimentoController {

    private final AlimentoService service;

    public AlimentoController(AlimentoService service) {
        this.service = service;
    }

    /** RF03: lista y busca por categoria o por texto. Publico. */
    @GetMapping
    public List<AlimentoResponse> listar(@RequestParam(required = false) Categoria categoria,
            @RequestParam(required = false) @Size(max = 50) String q) {
        return service.listar(categoria, q);
    }

    /** RF04 (show): ficha con consejos de conservacion y precios recientes. Publico. */
    @GetMapping("/{id}")
    public AlimentoFichaResponse ficha(@PathVariable Long id) {
        return service.ficha(id);
    }

    /** RF05: calendario anual del alimento en un departamento. Publico. */
    @GetMapping("/{id}/calendario")
    public CalendarioResponse calendario(@PathVariable Long id, @RequestParam String departamento) {
        return service.calendario(id, departamento);
    }

    /** RF07 (create). Requiere token ADMIN. Responde 201 con la cabecera Location. */
    @PostMapping
    public ResponseEntity<AlimentoResponse> crear(@Valid @RequestBody AlimentoRequest req) {
        AlimentoResponse creado = service.crear(req);
        return ResponseEntity.created(URI.create("/api/v1/alimentos/" + creado.id())).body(creado);
    }

    /** RF07 (update). Requiere token ADMIN. */
    @PutMapping("/{id}")
    public AlimentoResponse actualizar(@PathVariable Long id, @Valid @RequestBody AlimentoRequest req) {
        return service.actualizar(id, req);
    }

    /** RF07 (delete). Requiere token ADMIN. Responde 204 sin cuerpo. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
