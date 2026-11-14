package pe.upn.perudeestacion.exception;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pe.upn.perudeestacion.config.BaseDatosInicializador;

/**
 * Manejo global de errores. Todas las respuestas de error usan el formato estandar
 * ProblemDetail (RFC 9457, content-type application/problem+json).
 * ResponseEntityExceptionHandler ya resuelve los errores propios de Spring MVC:
 * JSON mal formado (400), tipo de parametro invalido (400), ruta inexistente (404), etc.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String BASE = "https://perudeestacion.pe/errores/";

    private ProblemDetail problema(HttpStatus status, String titulo, String detalle, String tipo) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalle);
        pd.setTitle(titulo);
        pd.setType(URI.create(BASE + tipo));
        return pd;
    }

    /** 400: fallan las anotaciones de validacion de un @RequestBody. Devuelve el detalle por campo. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        ex.getBindingResult().getGlobalErrors()
                .forEach(ge -> errores.putIfAbsent(ge.getObjectName(), ge.getDefaultMessage()));
        ProblemDetail pd = problema(HttpStatus.BAD_REQUEST, "Datos invalidos",
                "Uno o mas campos no cumplen las validaciones", "validacion");
        pd.setProperty("errores", errores);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail noEncontrado(RecursoNoEncontradoException ex) {
        return problema(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage(), "no-encontrado");
    }

    @ExceptionHandler(ConflictoException.class)
    public ProblemDetail conflicto(ConflictoException ex) {
        return problema(HttpStatus.CONFLICT, "Conflicto con datos existentes", ex.getMessage(), "conflicto");
    }

    /** 409: la base de datos rechazo el cambio (UNIQUE, FK o CHECK). Respaldo de las validaciones del servicio. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridad(DataIntegrityViolationException ex) {
        log.warn("Violacion de integridad: {}", ex.getMostSpecificCause().getMessage());
        return problema(HttpStatus.CONFLICT, "Conflicto con datos existentes",
                "La operacion viola una restriccion de la base de datos (duplicado o referencia en uso)", "conflicto");
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ProblemDetail credenciales(CredencialesInvalidasException ex) {
        return problema(HttpStatus.UNAUTHORIZED, "No autenticado", ex.getMessage(), "no-autenticado");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail accesoDenegado(AccessDeniedException ex) {
        return problema(HttpStatus.FORBIDDEN, "Acceso denegado", "No tiene permisos para esta operacion", "prohibido");
    }

    /** 503: la base de datos no responde (por ejemplo, proyecto de Supabase pausado). La app sigue en pie. */
    @ExceptionHandler({ CannotCreateTransactionException.class, DataAccessResourceFailureException.class })
    public ProblemDetail baseDatosNoDisponible(Exception ex) {
        log.error("Base de datos no disponible. {}", BaseDatosInicializador.pista(ex));
        return problema(HttpStatus.SERVICE_UNAVAILABLE, "Servicio no disponible",
                "La base de datos no responde en este momento. Intente nuevamente en unos minutos.", "no-disponible");
    }

    /** 500: cualquier error no previsto. Se registra en el log y nunca se expone la traza al cliente. */
    @ExceptionHandler(Exception.class)
    public ProblemDetail inesperado(Exception ex) {
        log.error("Error no controlado", ex);
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
                "Ocurrio un error inesperado. Intente nuevamente.", "interno");
    }
}
