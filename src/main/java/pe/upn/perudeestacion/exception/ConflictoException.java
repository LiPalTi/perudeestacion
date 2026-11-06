package pe.upn.perudeestacion.exception;

/** Se lanza cuando la operacion choca con un dato existente (HTTP 409). */
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
