package pe.upn.perudeestacion.exception;

/** Se lanza cuando un recurso pedido por id o codigo no existe (HTTP 404). */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String recurso, Object id) {
        super(recurso + " con identificador '" + id + "' no existe");
    }
}
