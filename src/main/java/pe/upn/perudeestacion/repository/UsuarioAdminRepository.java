package pe.upn.perudeestacion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.upn.perudeestacion.entity.UsuarioAdmin;

public interface UsuarioAdminRepository extends JpaRepository<UsuarioAdmin, Long> {

    Optional<UsuarioAdmin> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
