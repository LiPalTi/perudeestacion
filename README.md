# Perú de Estación: backend (Semana 3)

[![CI](https://github.com/LiPalTi/perudeestacion/actions/workflows/ci.yml/badge.svg)](https://github.com/LiPalTi/perudeestacion/actions/workflows/ci.yml)

API REST en Spring Boot con persistencia en Supabase (PostgreSQL). Muestra qué alimentos están en temporada por departamento y mes, e incluye el módulo piloto El Niño (Tumbes, Piura, Lambayeque), un simulador de ración mínima y un directorio de programas de apoyo.

Entregable de la Semana 3: API REST funcional con persistencia, CRUD, validaciones, seguridad básica, pruebas de endpoints y evidencias del backlog/sprint.

## Herramientas

Todas las herramientas son las vistas en el curso.

| Necesidad | Herramienta |
|---|---|
| IDE | Spring Tool Suite o VS Code con Extension Pack for Java |
| Lenguaje y framework | Java 21 o 25, Spring Boot 4.1.2 (igual que el demo del curso) |
| Persistencia | Spring Data JPA (JpaRepository, @Query, JPQL, @NamedQuery, EntityManager) |
| Base de datos | Supabase (PostgreSQL gestionado), usado solo como base de datos |
| Seguridad | Spring Security, JWT firmado con OAuth2 Resource Server, BCrypt, CORS para Angular |
| Pruebas de endpoints | Postman (colección con aserciones y Collection Runner) |
| Gestión | Jira o Trello (backlog), GitHub (código e issues) |

## Estructura del proyecto

```
perudeestacion/
  pom.xml
  .env.example                  plantilla de variables (copiar a .env)
  src/main/java/pe/upn/perudeestacion/
    config/                     seguridad (JWT, CORS) y preparación automática de la base
    controller/                 11 controladores REST (/api/v1/...)
    dto/                        records de entrada (con validaciones) y salida
    entity/                     14 entidades JPA y 6 enums
    exception/                  errores en formato ProblemDetail (RFC 9457)
    repository/                 interfaces JpaRepository y un repositorio con EntityManager
    security/                   login, emisión de token, creación del admin
    service/                    reglas de negocio y transacciones
  src/main/resources/
    application.properties
    db/01_esquema.sql           14 tablas, restricciones, índices y RLS
    db/02_datos_iniciales.sql   25 departamentos, 12 alimentos, alerta ENFEN 19-2026, etc.
  src/test/java/...             prueba unitaria de validaciones
  docs/postman/                 colección de pruebas (38 peticiones)
  docs/gestion/                 backlog.csv y plan_proyecto.md
  .github/workflows/            integración continua y tarea para mantener Supabase activo
```

## Paso a paso

### Paso 1. Crear el proyecto en Supabase (10 min)

1. Entra a supabase.com, inicia sesión con GitHub y pulsa New project.
2. Nombre: perudeestacion. Región: South America (São Paulo), que es la más cercana a Perú.
3. Escribe una contraseña de base de datos sin caracteres raros (letras y números) y guárdala. Es tu DB_PASSWORD.
4. Espera a que el proyecto termine de crearse.

### Paso 2. Tablas y datos de prueba (automático)

No necesitas ejecutar nada a mano. Al arrancar, la aplicación revisa la base de datos:

- Si no existen las tablas, ejecuta `db/01_esquema.sql` (14 tablas, restricciones, índices y RLS).
- Si las tablas están vacías, ejecuta `db/02_datos_iniciales.sql` (25 departamentos, 12 alimentos, 360 temporadas, alerta ENFEN 19-2026, programas y parámetros del simulador).
- Cada script se aplica completo o no se aplica, así que un corte a la mitad no deja la base a medias.

Si prefieres hacerlo a mano, ejecuta los dos scripts en ese orden en el SQL Editor de Supabase; la aplicación detecta que ya existen y no los repite. Para desactivar la revisión, define `DB_AUTO_INIT=false`.

El esquema activa Row Level Security en todas las tablas sin crear políticas. Así nadie puede leer las tablas por la API pública de Supabase; solo tu backend, que se conecta con el usuario postgres. Si Supabase muestra advertencias de RLS en el Security Advisor, es lo esperado.

Los precios, centros y temporadas de prueba están marcados como datos de prueba. Antes del cierre conviene reemplazarlos con datos de SISAP-MIDAGRI usando el CRUD.

### Paso 3. Obtener la cadena de conexión (3 min)

1. Pulsa el botón Connect en la parte superior del panel.
2. Elige la pestaña de conexión por JDBC y la opción Session pooler. Esta opción funciona en redes IPv4, como la mayoría de redes domésticas y universitarias.
3. Verás algo como `jdbc:postgresql://aws-0-sa-east-1.pooler.supabase.com:5432/postgres?user=postgres.abcdefgh...`.
4. De ahí separa tres valores:
   - DB_URL: `jdbc:postgresql://aws-0-sa-east-1.pooler.supabase.com:5432/postgres?sslmode=require&prepareThreshold=0` (con el host que te muestre Supabase)
   - DB_USER: `postgres.abcdefgh` (incluye el punto y la referencia del proyecto)
   - DB_PASSWORD: la contraseña del paso 1

### Paso 4. Abrir el proyecto en el IDE (5 min)

En Spring Tool Suite, usa File, Import, Maven, Existing Maven Projects y elige la carpeta `perudeestacion`. Espera a que descargue las dependencias.

En VS Code, abre la carpeta. La extensión Spring Boot Dashboard detecta el proyecto.

Verifica que el JDK sea 21 o superior. Si usas JDK 25 como en el demo del curso, puedes cambiar `<java.version>` a 25 en el pom.xml.

### Paso 5. Configurar las variables (2 min)

1. Copia `.env.example` y renómbralo como `.env`, en la raíz del proyecto, junto al pom.xml.
2. Completa sus valores:
   - DB_URL, DB_USER y DB_PASSWORD del paso 3.
   - JWT_SECRET: una frase larga de al menos 32 caracteres.
   - ADMIN_EMAIL y ADMIN_PASSWORD: el usuario administrador que se creará al arrancar.

El archivo `.env` está en `.gitignore`, así que nunca se sube a GitHub. Spring lo lee gracias a `spring.config.import`; también acepta `.env.txt`, el nombre que a veces deja el Bloc de notas de Windows.

### Paso 6. Ejecutar (2 min)

1. Ejecuta `PeruDeEstacionApplication` (Run As, Spring Boot App) o usa `./mvnw spring-boot:run`.
2. En la consola deben aparecer `Base de datos lista`, `Administrador inicial creado: admin@...` (solo la primera vez) y `Tomcat started on port 8080`.
3. Prueba en el navegador `http://localhost:8080/api/v1/departamentos`. Debe mostrar los 25 departamentos en JSON.

### Paso 7. Probar con Postman (10 min)

1. En Postman usa Import y elige `docs/postman/PeruDeEstacion.postman_collection.json`.
2. Abre la colección, pestaña Variables, y escribe tu ADMIN_PASSWORD en la columna Current value de `adminPassword`. Current value no se exporta.
3. Pulsa Run collection y luego Run. Las peticiones deben ejecutarse en orden; el login guarda el token automáticamente para las siguientes.
4. Toma captura del resumen del Runner, con todas las pruebas en verde. Es tu evidencia de pruebas de endpoints.

La colección está organizada en cinco carpetas:
- Públicos.
- Autenticación.
- Administración con JWT: crea, actualiza y borra un alimento de prueba, por lo que se puede ejecutar varias veces.
- Simulador.
- CORS.

Incluye casos negativos: mes 13 (400), id inexistente (404), sin token (401), clave errada (401), duplicado (409), datos inválidos (400), segunda alerta vigente (409) y origen CORS no permitido (403).

### Paso 8. GitHub y backlog

1. El código está en https://github.com/LiPalTi/perudeestacion. Para trabajar en tu computadora: `git clone https://github.com/LiPalTi/perudeestacion.git`, y luego `git add .`, `git commit -m "HUxx descripción"` y `git push` con cada avance. El archivo `.env` nunca se sube.
2. Cada `push` ejecuta la integración continua (pestaña Actions): compila, crea una base PostgreSQL limpia, arranca la API, valida las entidades contra las tablas y corre la colección de Postman. El resultado queda en la insignia CI de este README y los registros en el artefacto `resultados-pruebas`.
3. Importa `docs/gestion/backlog.csv`:
   - En Jira, usa Configuración del sistema, Importación externa, CSV, y asigna las columnas Resumen, Tipo de incidencia, Story Points y Sprint.
   - En Trello, crea una lista por estado y copia las tarjetas.
   - También puedes crear los issues en GitHub Projects.
4. Mueve a Hecho cada historia del Sprint 1 cuando su prueba de Postman pase.

### Mantener activo el proyecto de Supabase (plan gratuito)

Supabase pausa los proyectos gratuitos con poca actividad durante 7 días. El flujo `.github/workflows/mantener-supabase-activo.yml` hace dos consultas de lectura al día para evitarlo. Para activarlo:

1. En Supabase pulsa Connect, elige Session pooler y copia la cadena en formato URI: `postgresql://postgres.<ref>:<contraseña>@<host>:5432/postgres`.
2. En GitHub entra a Settings, Secrets and variables, Actions, New repository secret. Nombre: `SUPABASE_DB_URL`; valor: la cadena anterior.
3. En la pestaña Actions abre "Mantener Supabase activo" y pulsa Run workflow para probarlo.

GitHub desactiva las tareas programadas de un repositorio público si pasan 60 días sin cambios en él; basta con volver a activarla desde Actions. Si aun así el proyecto se pausa, entra al panel de Supabase y pulsa Resume project.

### Paso 9. Evidencias para el informe técnico

| Evidencia | Cómo obtenerla |
|---|---|
| Persistencia | Captura de Table Editor de Supabase con las tablas y datos |
| Modelo de datos | Diagrama de la base de datos en Supabase (Database, Schema Visualizer) o Draw.io |
| CRUD | Capturas de Postman: POST 201, PUT 200, DELETE 204 y GET 404 posterior |
| Validaciones | Captura de una respuesta 400 con el campo `errores` |
| Seguridad | Captura del 401 sin token, del login con token y del token decodificado en jwt.io |
| Pruebas | Resumen del Collection Runner |
| Backlog y sprint | Tablero de Jira o Trello con el Sprint 1 y el plan de `docs/gestion/plan_proyecto.md` |
| Código | Enlace al repositorio de GitHub |

## Endpoints (paths)

Base: `http://localhost:8080/api/v1`. Los GET son públicos (excepto parámetros del simulador). Las escrituras requieren la cabecera `Authorization: Bearer <token>` con rol ADMIN.

| Método | Path | Acceso | Descripción |
|---|---|---|---|
| POST | /auth/login | Público | Devuelve el token JWT (60 min) |
| GET | /departamentos | Público | Lista de 25 departamentos |
| GET | /departamentos/{codigo} | Público | Detalle con alerta vigente e impactos (ej. PIU) |
| GET | /alimentos?categoria=&q= | Público | Lista con filtro por categoría y nombre |
| GET | /alimentos/{id} | Público | Ficha con consejos y últimos precios |
| GET | /alimentos/{id}/calendario?departamento= | Público | Nivel de temporada en los 12 meses |
| POST, PUT, DELETE | /alimentos, /alimentos/{id} | ADMIN | CRUD de alimentos |
| GET | /temporadas?departamento=&mes=&categoria= | Público | Consulta principal, ordenada por nivel |
| GET | /temporadas/{id} | Público | Una temporada |
| POST, PUT, DELETE | /temporadas, /temporadas/{id} | ADMIN | CRUD de temporadas |
| GET | /precios?alimento=&departamento= | Público | Precios de referencia |
| POST, PUT, DELETE | /precios, /precios/{id} | ADMIN | CRUD de precios |
| GET | /alertas?vigente= | Público | Alertas ENFEN |
| GET | /alertas/{id}, /alertas/{id}/impactos | Público | Alerta y sus impactos regionales |
| POST, PUT, DELETE | /alertas, /alertas/{id} | ADMIN | CRUD de alertas (solo una vigente) |
| GET | /impactos/{id} | Público | Impacto regional con alimentos afectados |
| POST, PUT, DELETE | /impactos, /impactos/{id} | ADMIN | CRUD de impactos |
| GET | /programas-apoyo, /programas-apoyo/{id} | Público | Programas con requisitos |
| POST, PUT, DELETE | /programas-apoyo, /programas-apoyo/{id} | ADMIN | CRUD de programas |
| GET | /centros-apoyo?departamento=&programa= | Público | Centros de apoyo |
| POST, PUT, DELETE | /centros-apoyo, /centros-apoyo/{id} | ADMIN | CRUD de centros |
| POST | /simulaciones/racion | Público | Calcula la ración mínima y la guarda |
| GET | /parametros-simulador | ADMIN | Parámetros del cálculo |
| PUT | /parametros-simulador/{clave} | ADMIN | Cambia un parámetro |

Códigos de respuesta usados: 200, 201 (con cabecera Location), 204, 400, 401, 403, 404, 409, 500 y 503 (base de datos no disponible). Todos los errores usan el formato ProblemDetail (`application/problem+json`).

## Problemas frecuentes

La aplicación ya resuelve o explica por sí misma los problemas más comunes:

| Situación | Qué hace la aplicación | Qué hacer tú |
|---|---|---|
| Base de datos nueva o recién restaurada, sin tablas o sin datos | Crea las tablas y carga los datos de prueba al arrancar | Nada |
| Proyecto de Supabase pausado o sin conexión | Arranca igual, escribe en el log la causa probable y responde 503 hasta que la base vuelva | Pulsa Resume project en Supabase; activa la tarea "Mantener Supabase activo" |
| Usuario sin la referencia del proyecto (`Tenant or user not found`) | Lo indica en el log | DB_USER debe ser `postgres.<ref>` |
| Contraseña incorrecta (`password authentication failed`) | Lo indica en el log | Revisa DB_USER y DB_PASSWORD en `.env` |
| `prepared statement "S_1" already exists` (puerto 6543) | La URL de ejemplo ya incluye `prepareThreshold=0` | Usa el puerto 5432 o conserva ese parámetro |
| Red solo IPv4 | El Session pooler funciona en IPv4 | Usa la cadena del Session pooler, no la conexión directa |
| JWT_SECRET vacío o corto | Usa una clave temporal o deriva una de 256 bits, y lo avisa en el log | Define una clave de 32 caracteres o más |
| Conexiones cerradas por el pooler tras un rato sin uso | Hikari las comprueba cada 2 minutos y las renueva antes de que caduquen | Nada |
| `.env` guardado como `.env.txt` | Lo lee igual | Nada |
| `./mvnw: Permission denied` en Mac o Linux | El archivo ya tiene permiso de ejecución en el repositorio | Si copiaste el proyecto de otra forma: `chmod +x mvnw` |
| `Could not resolve placeholder 'DB_URL'` | No encuentra el `.env` | Ponlo junto al `pom.xml`; en STS también puedes definir las variables en Run Configurations, Environment |
| 401 en todas las escrituras | El token dura 60 minutos (`JWT_EXPIRATION_MINUTES`) | Ejecuta de nuevo el login en Postman |

## Verificación

Cada cambio en GitHub pasa por la integración continua (`.github/workflows/ci.yml`): compilación con Maven, prueba unitaria, arranque contra PostgreSQL 16 con creación automática de tablas y datos, arranque con `DDL_AUTO=validate` para comprobar que las 14 entidades coinciden con las tablas, y ejecución de la colección de Postman con Newman. El estado actual se ve en la insignia CI al inicio de este archivo.
