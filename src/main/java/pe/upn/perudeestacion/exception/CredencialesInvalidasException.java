package pe.upn.perudeestacion.exception;

/** Login fallido (HTTP 401). El mensaje es generico para no revelar si el correo existe. */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Correo o contrasena incorrectos");
    }
}
