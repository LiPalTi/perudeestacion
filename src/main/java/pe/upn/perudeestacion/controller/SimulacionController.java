package pe.upn.perudeestacion.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.upn.perudeestacion.dto.RacionRequest;
import pe.upn.perudeestacion.dto.RacionResponse;
import pe.upn.perudeestacion.service.SimulacionService;

@RestController
@RequestMapping("/api/v1/simulaciones")
public class SimulacionController {

    private final SimulacionService service;

    public SimulacionController(SimulacionService service) {
        this.service = service;
    }

    /** RF10: calcula la racion minima del hogar. Publico. Guarda la simulacion anonima (201). */
    @PostMapping("/racion")
    public ResponseEntity<RacionResponse> racion(@Valid @RequestBody RacionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.calcular(req));
    }
}
