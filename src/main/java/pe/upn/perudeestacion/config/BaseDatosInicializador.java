package pe.upn.perudeestacion.config;

import java.util.Locale;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Prepara la base de datos al arrancar, para que un proyecto de Supabase nuevo o restaurado funcione sin pasos
 * manuales: si no existen las tablas ejecuta db/01_esquema.sql, si estan vacias ejecuta db/02_datos_iniciales.sql
 * y si no hay simulaciones carga las de ejemplo de db/03_simulaciones_prueba.sql.
 * Cada script corre en una sola transaccion: o se aplica completo o no se aplica nada.
 * Si la base no responde (proyecto pausado, credenciales erroneas) la app arranca igual, registra la causa probable
 * y las peticiones responden 503 hasta que la base vuelva.
 */
@Component
@Order(1)
public class BaseDatosInicializador implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BaseDatosInicializador.class);

    private final DataSource dataSource;
    private final boolean autoInit;

    public BaseDatosInicializador(DataSource dataSource, @Value("${app.db.auto-init:true}") boolean autoInit) {
        this.dataSource = dataSource;
        this.autoInit = autoInit;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!autoInit) {
            log.info("DB_AUTO_INIT=false: no se revisan las tablas al arrancar");
            return;
        }
        try {
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            Boolean existe = jdbc.queryForObject("SELECT to_regclass('public.departamento') IS NOT NULL", Boolean.class);
            if (!Boolean.TRUE.equals(existe)) {
                log.info("Base de datos sin tablas: ejecutando db/01_esquema.sql");
                ejecutar("db/01_esquema.sql");
            }
            Integer departamentos = jdbc.queryForObject("SELECT count(*) FROM departamento", Integer.class);
            if (departamentos != null && departamentos == 0) {
                log.info("Tablas vacias: ejecutando db/02_datos_iniciales.sql");
                ejecutar("db/02_datos_iniciales.sql");
            }
            Integer simulaciones = jdbc.queryForObject("SELECT count(*) FROM simulacion", Integer.class);
            if (simulaciones != null && simulaciones == 0) {
                log.info("Sin simulaciones: ejecutando db/03_simulaciones_prueba.sql");
                ejecutar("db/03_simulaciones_prueba.sql");
            }
            log.info("Base de datos lista");
        } catch (DataAccessException | TransactionException e) {
            log.error("No se pudo preparar la base de datos. {}", pista(e));
        }
    }

    private void ejecutar(String ruta) {
        ResourceDatabasePopulator script = new ResourceDatabasePopulator(new ClassPathResource(ruta));
        script.setSqlScriptEncoding("UTF-8");
        new TransactionTemplate(new DataSourceTransactionManager(dataSource))
                .executeWithoutResult(estado -> script.execute(dataSource));
    }

    /** Traduce los errores de conexion mas frecuentes con Supabase a una indicacion concreta. */
    public static String pista(Throwable error) {
        StringBuilder mensajes = new StringBuilder();
        Throwable raiz = error;
        for (Throwable t = error; t != null; t = t.getCause()) {
            mensajes.append(' ').append(t.getClass().getSimpleName()).append(' ').append(t.getMessage());
            raiz = t;
        }
        String m = mensajes.toString().toLowerCase(Locale.ROOT);
        if (m.contains("tenant or user not found")) {
            return "Con el Session pooler el usuario debe ser postgres.<referencia-del-proyecto> (revise DB_USER).";
        }
        if (m.contains("password authentication failed")) {
            return "Usuario o contrasena incorrectos (revise DB_USER y DB_PASSWORD en el archivo .env).";
        }
        if (m.contains("prepared statement")) {
            return "Use el puerto 5432 (Session pooler) o agregue &prepareThreshold=0 al final de DB_URL.";
        }
        if (m.contains("unknownhost") || m.contains("network is unreachable") || m.contains("connection refused")
                || m.contains("timed out") || m.contains("timeout") || m.contains("connection attempt failed")) {
            return "No hay conexion con Supabase: verifique que el proyecto no este pausado (Resume project) "
                    + "y que DB_URL use el Session pooler, que funciona en redes IPv4.";
        }
        return "Detalle: " + raiz.getMessage();
    }
}
