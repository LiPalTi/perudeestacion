# Perú de Estación: backend (Semana 3)

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
    config/SecurityConfig       reglas de acceso, JWT, CORS
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
```

## Paso a paso

### Paso 1. Crear el proyecto en Supabase (10 min)

1. Entra a supabase.com, inicia sesión con GitHub y pulsa New project.
2. Nombre: perudeestacion. Región: South America (São Paulo), que es la más cercana a Perú.
3. Escribe una contraseña de base de datos sin caracteres raros (letras y números) y guárdala. Es tu DB_PASSWORD.
4. Espera a que el proyecto termine de crearse.

### Paso 2. Crear las tablas y cargar los datos (5 min)

1. En el menú izquierdo abre SQL Editor y pulsa New query.
2. Copia todo `src/main/resources/db/01_esquema.sql`, pégalo y pulsa Run. Debe decir "Success. No rows returned".
3. Abre otra consulta con `02_datos_iniciales.sql` y pulsa Run.
4. Comprueba en Table Editor que aparecen 14 tablas. `departamento` debe tener 25 filas y `temporada` 360.

El script activa Row Level Security en todas las tablas sin crear políticas. Así nadie puede leer las tablas por la API pública de Supabase; solo tu backend, que se conecta con el usuario postgres. Si Supabase muestra advertencias de RLS en el Security Advisor, es lo esperado.

Los precios, centros y temporadas de prueba están marcados como datos de prueba. Antes del cierre conviene reemplazarlos con datos de SISAP-MIDAGRI usando el CRUD.

### Paso 3. Obtener la cadena de conexión (3 min)

1. Pulsa el botón Connect en la parte superior del panel.
2. Elige la pestaña de conexión por JDBC y la opción Session pooler. Esta opción funciona en redes IPv4, como la mayoría de redes domésticas y universitarias.
3. Verás algo como `jdbc:postgresql://aws-0-sa-east-1.pooler.supabase.com:5432/postgres?user=postgres.abcdefgh...`.
4. De ahí separa tres valores:
   - DB_URL: `jdbc:postgresql://aws-0-sa-east-1.pooler.supabase.com:5432/postgres?sslmode=require`
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

El archivo `.env` está en `.gitignore`, así que nunca se sube a GitHub. Spring lo lee gracias a `spring.config.import=optional:file:.env[.properties]`.

### Paso 6. Ejecutar (2 min)

1. Ejecuta `PeruDeEstacionApplication` (Run As, Spring Boot App) o usa `./mvnw spring-boot:run`.
2. En la consola deben aparecer dos líneas: `Administrador inicial creado: admin@...` y `Tomcat started on port 8080`.
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

### Paso 8. Subir a GitHub y registrar el backlog (15 min)

1. Crea un repositorio en GitHub y sube el proyecto: `git init`, `git add .`, `git commit -m "HU01-HU13 backend Sprint 1"`, `git remote add origin ...` y `git push`. Revisa que `.env` no aparezca en el repositorio.
2. Importa `docs/gestion/backlog.csv`:
   - En Jira, usa Configuración del sistema, Importación externa, CSV, y asigna las columnas Resumen, Tipo de incidencia, Story Points y Sprint.
   - En Trello, crea una lista por estado y copia las tarjetas.
   - También puedes crear los issues en GitHub Projects.
3. Mueve a Hecho cada historia del Sprint 1 cuando su prueba de Postman pase. Toma captura del tablero.

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

Códigos de respuesta usados: 200, 201 (con cabecera Location), 204, 400, 401, 403, 404, 409 y 500. Todos los errores usan el formato ProblemDetail (`application/problem+json`).

## Problemas frecuentes

| Síntoma | Causa probable | Solución |
|---|---|---|
| `Schema-validation: missing table` o `wrong column type` al arrancar | Los scripts SQL no se ejecutaron completos, o hay una diferencia de tipo | Vuelve a ejecutar 01 y 02. Si persiste, cambia temporalmente `spring.jpa.hibernate.ddl-auto=none` para avanzar y anota la columna que falla |
| `Connection refused`, `UnknownHost` o timeout | Usaste la conexión directa (solo IPv6) o la red bloquea el puerto | Usa el Session pooler del paso 3; prueba con datos móviles |
| `password authentication failed` | DB_USER sin la referencia del proyecto | El usuario es `postgres.<ref>`, no solo `postgres` |
| `prepared statement "S_1" already exists` | Estás usando el puerto 6543 (modo transacción) | Usa el puerto 5432 o agrega `&prepareThreshold=0` a la URL |
| El proyecto de Supabase no responde | El plan gratuito pausa el proyecto tras 7 días sin actividad | Entra al panel y pulsa Restore |
| Error al arrancar sobre la clave JWT | JWT_SECRET con menos de 32 caracteres | Usa una frase más larga |
| 401 en todas las escrituras | El token expiró (60 min) o no se envió | Ejecuta de nuevo el login en Postman |
| `Could not resolve placeholder DB_URL` | El archivo `.env` no está en la raíz o se llama `.env.txt` | Revisa el nombre y la ubicación; en STS también puedes poner las variables en Run Configurations, Environment |

## Nota sobre la verificación

Los scripts SQL se probaron sobre PostgreSQL 16. Se verificaron los conteos de datos, la consulta de Piura en noviembre y el índice que impide dos alertas vigentes.

El código Java se revisó a nivel de sintaxis, pero no se compiló en el entorno donde se preparó, porque no había acceso al repositorio de Maven. La primera compilación en tu equipo es la prueba real. Si aparece un error, copia el mensaje completo y se corrige puntualmente.
