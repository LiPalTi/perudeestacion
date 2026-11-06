package pe.upn.perudeestacion.dto;

public record LoginResponse(String token, String tipo, long expiraEnSegundos) {
}
