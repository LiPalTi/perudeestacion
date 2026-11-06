package pe.upn.perudeestacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import pe.upn.perudeestacion.dto.RacionRequest;
import pe.upn.perudeestacion.entity.CapacidadEconomica;
import pe.upn.perudeestacion.entity.TipoVivienda;

/** Prueba unitaria de las validaciones del simulador. No necesita base de datos. */
class RacionRequestValidacionTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void iniciar() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void cerrar() {
        factory.close();
    }

    @Test
    void solicitudValidaNoTieneErrores() {
        RacionRequest req = new RacionRequest("PIU", 2, 2, 0, 7, TipoVivienda.ADOBE, CapacidadEconomica.BAJA);
        assertTrue(validator.validate(req).isEmpty());
    }

    @Test
    void rechazaMenosDeTresDias() {
        RacionRequest req = new RacionRequest("PIU", 2, 0, 0, 2, TipoVivienda.ADOBE, CapacidadEconomica.BAJA);
        Set<ConstraintViolation<RacionRequest>> errores = validator.validate(req);
        assertEquals(1, errores.size());
        assertEquals("dias", errores.iterator().next().getPropertyPath().toString());
    }

    @Test
    void rechazaHogarSinPersonas() {
        RacionRequest req = new RacionRequest("PIU", 0, 0, 0, 5, TipoVivienda.MATERIAL_NOBLE, CapacidadEconomica.ALTA);
        assertEquals(1, validator.validate(req).size());
    }
}
