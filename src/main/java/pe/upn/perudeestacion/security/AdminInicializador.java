package pe.upn.perudeestacion.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;

import pe.upn.perudeestacion.config.BaseDatosInicializador;
import pe.upn.perudeestacion.entity.UsuarioAdmin;
import pe.upn.perudeestacion.repository.UsuarioAdminRepository;

/**
 * Crea el primer administrador al iniciar la app, leyendo ADMIN_EMAIL y ADMIN_PASSWORD del archivo .env.
 * Asi la contrasena nunca queda escrita en el codigo ni en los scripts SQL: solo se guarda su hash BCrypt.
 */
@Component
@Order(2)
public class AdminInicializador implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInicializador.class);

    private final UsuarioAdminRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public AdminInicializador(UsuarioAdminRepository usuarioRepository, PasswordEncoder passwordEncoder,
            @Value("${app.admin.email:}") String email, @Value("${app.admin.password:}") String password) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (email.isBlank() || password.isBlank()) {
            log.warn("ADMIN_EMAIL / ADMIN_PASSWORD no definidos: no se crea administrador inicial");
            return;
        }
        try {
            if (usuarioRepository.existsByEmailIgnoreCase(email)) {
                return;
            }
            UsuarioAdmin u = new UsuarioAdmin();
            u.setEmail(email.trim().toLowerCase());
            u.setPasswordHash(passwordEncoder.encode(password));
            u.setRol("ADMIN");
            u.setActivo(true);
            usuarioRepository.save(u);
            log.info("Administrador inicial creado: {}", u.getEmail());
        } catch (DataAccessException | TransactionException e) {
            log.warn("No se pudo crear el administrador inicial; se intentara en el proximo arranque. {}",
                    BaseDatosInicializador.pista(e));
        }
    }
}
