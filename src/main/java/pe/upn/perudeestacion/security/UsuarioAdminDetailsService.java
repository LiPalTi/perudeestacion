package pe.upn.perudeestacion.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pe.upn.perudeestacion.entity.UsuarioAdmin;
import pe.upn.perudeestacion.repository.UsuarioAdminRepository;

/**
 * UserDetailsService: Spring Security lo usa para obtener el usuario desde la tabla usuario_admin
 * (paso 3 del diagrama DaoAuthenticationProvider visto en clase).
 */
@Service
public class UsuarioAdminDetailsService implements UserDetailsService {

    private final UsuarioAdminRepository usuarioRepository;

    public UsuarioAdminDetailsService(UsuarioAdminRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioAdmin u = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return User.withUsername(u.getEmail())
                .password(u.getPasswordHash())
                .roles(u.getRol())                 // ADMIN -> autoridad ROLE_ADMIN
                .disabled(!Boolean.TRUE.equals(u.getActivo()))
                .build();
    }
}
