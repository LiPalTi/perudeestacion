package pe.upn.perudeestacion.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.upn.perudeestacion.dto.LoginRequest;
import pe.upn.perudeestacion.dto.LoginResponse;
import pe.upn.perudeestacion.exception.CredencialesInvalidasException;
import pe.upn.perudeestacion.security.TokenService;

/** POST /api/v1/auth/login: valida credenciales y devuelve el access token (flujo visto en clase). */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(req.email(), req.password()));
            return new LoginResponse(tokenService.generar(auth), "Bearer", tokenService.segundosDeVida());
        } catch (AuthenticationException ex) {
            throw new CredencialesInvalidasException();
        }
    }
}
