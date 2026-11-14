# Perú de Estación: API REST (entrega T1)

[![CI](https://github.com/LiPalTi/perudeestacion/actions/workflows/ci.yml/badge.svg)](https://github.com/LiPalTi/perudeestacion/actions/workflows/ci.yml)

API REST en Spring Boot con base de datos PostgreSQL en Supabase. Indica qué alimentos están en temporada por departamento y mes, e incluye el módulo piloto El Niño (Tumbes, Piura y Lambayeque), un simulador de ración mínima y un directorio de programas de apoyo.

Este repositorio es el backend que consume el frontend Angular. Para la evaluación T1 (integración frontend-backend) reúne:

| Exigencia de T1 | Dónde está |
|---|---|
| API REST que consume Angular | 40 operaciones bajo `/api/v1` (sección Endpoints) |
| Endpoints protegidos | Login con JWT y escrituras solo con rol ADMIN (sección Autenticación) |
| Manejo de errores | Formato único ProblemDetail con detalle por campo (sección Errores) |
| Validación de formularios | Reglas del backend que el formulario Angular debe replicar (sección Validaciones) |
| Pruebas de integración | Colección de Postman de 38 peticiones e integración continua en cada cambio |
| Datos para pruebas | Carpeta `docs/pruebas` y carga automática en la base |
| Documentación de consumo | Este archivo |

## Prueba rápida

Necesitas Java 21 o superior, una base PostgreSQL (un proyecto gratuito de Supabase sirve) y Postman.

1. **Base de datos.** En supabase.com crea un proyecto (New project, región São Paulo). Luego pulsa Connect, elige JDBC y Session pooler, y copia el host, el usuario `postgres.<ref>` y la contraseña.
2. **Credenciales.** Copia `.env.example` como `.env`, junto al `pom.xml`, y completa `DB_URL`, `DB_USER` y `DB_PASSWORD`.
3. **Ejecutar.** En la carpeta del proyecto: `./mvnw spring-boot:run` (en Windows, `mvnw.cmd spring-boot:run`). También puedes usar Run As, Spring Boot App en Spring Tool Suite.
4. **Comprobar.** La consola muestra `Base de datos lista` y `Tomcat started on port 8080`. En el navegador, `http://localhost:8080/api/v1/departamentos` devuelve los 25 departamentos.
5. **Probar.** En Postman importa la colección `docs/postman/PeruDeEstacion.postman_collection.json` y el entorno `docs/postman/PeruDeEstacion.postman_environment.json`. Elige el entorno "Peru de Estacion - local" y pulsa Run collection: deben pasar las 38 peticiones.

No hace falta crear tablas ni cargar datos a mano. Si la base está vacía, la aplicación crea las 14 tablas y carga los datos de prueba al arrancar. También sirve cualquier PostgreSQL local: en ese caso, `DB_URL=jdbc:postgresql://localhost:5432/postgres`.

## Datos para pruebas

Todo está en el proyecto. El detalle completo está en [`docs/pruebas/README.md`](docs/pruebas/README.md).

| Qué | Dónde |
|---|---|
| Scripts que cargan la base (la app los ejecuta sola) | `src/main/resources/db/01_esquema.sql`, `02_datos_iniciales.sql`, `03_simulaciones_prueba.sql` |
| Los mismos datos en CSV, una tabla por archivo | `docs/pruebas/datos/` |
| Cuerpos JSON de ejemplo para cada operación | `docs/pruebas/peticiones/` |
| Colección y entorno de Postman | `docs/postman/` |
| Script para volver a los datos iniciales | `docs/pruebas/reiniciar_datos.sql` |

**Usuario de prueba:** `admin@perudeestacion.pe` / `CambiaEstaClave2026`, los valores de `.env.example`. La aplicación lo crea al arrancar con `ADMIN_EMAIL` y `ADMIN_PASSWORD`.

| Tabla | Registros | Tabla | Registros |
|---|---|---|---|
| departamento | 25 | alerta_nino | 1 |
| alimento | 12 | impacto_region / impacto_alimento | 3 / 12 |
| temporada | 360 | programa_apoyo / requisito | 5 / 10 |
| precio_referencia | 10 | centro_apoyo | 4 |
| consejo_conservacion | 5 | parametro_simulador | 18 |
| simulacion | 8 | usuario_admin | 1 |

Consultas con resultado asegurado:
- `GET /api/v1/temporadas?departamento=PIU&mes=11` devuelve mango y plátano en temporada alta, junto con la alerta de El Niño.
- `GET /api/v1/departamentos/PIU` devuelve los impactos en Piura.
- `POST /api/v1/simulaciones/racion` con 4 adultos y 7 días calcula 58 800 kcal y S/ 108,83.

Los precios, temporadas y centros son datos de prueba. En producción se reemplazan con datos de SISAP-MIDAGRI desde el CRUD.

## Guía de consumo para Angular

### Conexión

```ts
// src/environments/environment.ts
export const environment = { apiUrl: 'http://localhost:8080/api/v1' };
```

La API ya acepta peticiones desde `http://localhost:4200` (CORS). Para un dominio desplegado, agrégalo en `CORS_ORIGINS`, separado por comas.

### Autenticación

1. `POST /auth/login` con `{ "email": "...", "password": "..." }` responde `{ "token": "...", "tipo": "Bearer", "expiraEnSegundos": 3600 }`.
2. Guarda el token (por ejemplo, en `sessionStorage`) y envíalo en cada escritura con la cabecera `Authorization: Bearer <token>`.
3. Si la API responde 401, el token venció o falta: vuelve a la pantalla de login.

```ts
// Interceptor que agrega el token a las peticiones hacia la API
export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const token = sessionStorage.getItem('token');
  return next(token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req);
};
```

Las consultas GET, el login y el simulador son públicos. Crear, editar y eliminar, y también ver los parámetros del simulador, exigen el rol ADMIN.

### Errores

Todos los errores tienen el mismo formato (`application/problem+json`). Cuando falla una validación, `errores` trae el mensaje por campo, listo para mostrarlo debajo de cada input:

```json
{
  "type": "https://perudeestacion.pe/errores/validacion",
  "title": "Datos invalidos",
  "status": 400,
  "detail": "Uno o mas campos no cumplen las validaciones",
  "errores": { "dias": "dias debe ser al menos 3 (caja de reserva INDECI)" }
}
```

| Código | Cuándo | Qué hacer en la interfaz |
|---|---|---|
| 400 | Datos inválidos | Mostrar `errores[campo]` junto a cada campo |
| 401 | Sin token, token vencido o credenciales incorrectas | Ir al login |
| 403 | Token sin permiso u origen no permitido | Mensaje de acceso denegado |
| 404 | El registro no existe | Mensaje "no encontrado" y volver a la lista |
| 409 | Duplicado, segunda alerta vigente o registro en uso | Mostrar `detail` |
| 503 | La base de datos no responde | Mensaje "intente en unos minutos" |
| 500 | Error inesperado | Mensaje genérico |

### Validaciones

El backend aplica estas reglas; si el formulario Angular las replica, el usuario ve el error antes de enviar.

| Formulario | Campo | Regla |
|---|---|---|
| Login | email, password | Obligatorios; email con formato válido |
| Simulador | departamento | Código de 3 letras (PIU, TUM, LAM...) |
| Simulador | adultos, ninos, adultosMayores | Entre 0 y 20; al menos una persona en total |
| Simulador | dias | Entre 3 y 30 |
| Simulador | tipoVivienda | MATERIAL_NOBLE, ADOBE, QUINCHA, ESTERA o MADERA |
| Simulador | capacidadEconomica | BAJA, MEDIA o ALTA |
| Alimento | nombre | Obligatorio, hasta 80 caracteres, sin repetir |
| Alimento | categoria | FRUTA, VERDURA, TUBERCULO o PESCADO |
| Alimento | imagenUrl | Opcional; debe empezar con http:// o https:// |
| Temporada | mes | Entre 1 y 12 |
| Temporada | nivel | ALTA, MEDIA o BAJA; una sola por alimento, departamento y mes |
| Precio | precioKg | Mayor que 0, con 2 decimales como máximo |
| Precio | fecha, fuente | Fecha no futura; fuente obligatoria |

### Respuesta de la consulta principal

`GET /temporadas?departamento=PIU&mes=11` (resumida):

```json
{
  "departamento": { "id": 20, "codigo": "PIU", "nombre": "Piura", "regionNatural": "COSTA" },
  "mes": 11,
  "alertaVigente": { "id": 1, "comunicado": "ENFEN 19-2026", "fecha": "2026-10-30",
                     "estado": "Alerta de El Nino Costero", "magnitud": "Extraordinaria", "vigente": true },
  "resultados": [
    { "alimentoId": 1, "nombre": "Mango", "categoria": "FRUTA", "imagenUrl": null,
      "nivelTemporada": "ALTA", "riesgoAlza": "MEDIO", "precioRefKg": 3.50,
      "fuentePrecio": "Dato de prueba (reemplazar con SISAP-MIDAGRI)", "fechaPrecio": "2026-10-20" }
  ]
}
```

## Endpoints

Base: `http://localhost:8080/api/v1`.

| Método | Path | Acceso | Descripción |
|---|---|---|---|
| POST | /auth/login | Público | Devuelve el token JWT (60 min) |
| GET | /departamentos, /departamentos/{codigo} | Público | Lista y detalle con alerta e impactos |
| GET | /alimentos?categoria=&q=, /alimentos/{id} | Público | Lista con filtros y ficha con consejos y precios |
| GET | /alimentos/{id}/calendario?departamento= | Público | Nivel de temporada en los 12 meses |
| GET | /temporadas?departamento=&mes=&categoria=, /temporadas/{id} | Público | Consulta principal, ordenada por nivel |
| GET | /precios?alimento=&departamento= | Público | Precios de referencia |
| GET | /alertas?vigente=, /alertas/{id}, /alertas/{id}/impactos | Público | Alertas ENFEN e impactos |
| GET | /impactos/{id} | Público | Impacto con alimentos afectados |
| GET | /programas-apoyo, /programas-apoyo/{id} | Público | Programas con requisitos |
| GET | /centros-apoyo?departamento=&programa= | Público | Centros de apoyo |
| POST | /simulaciones/racion | Público | Calcula la ración mínima y la guarda |
| POST, PUT, DELETE | /alimentos, /temporadas, /precios, /alertas, /impactos, /programas-apoyo, /centros-apoyo (y /{id}) | ADMIN | CRUD |
| GET, PUT | /parametros-simulador, /parametros-simulador/{clave} | ADMIN | Parámetros del simulador |

Crear responde 201 con la cabecera `Location`, actualizar responde 200 y eliminar responde 204.

## Pruebas de integración

| Prueba | Cómo se ejecuta | Qué cubre |
|---|---|---|
| Colección de Postman | Postman, Run collection | 38 peticiones y 69 aserciones: consultas, login, CRUD con token, validaciones (400), sin token (401), no encontrado (404), duplicados (409), simulador y CORS |
| Integración continua | Automática en cada cambio (pestaña Actions) | Compila, crea una base PostgreSQL limpia, carga los datos de prueba, comprueba que las entidades coinciden con las tablas, corre la colección de Postman y verifica que con la base caída la API responde 503 |
| Prueba unitaria | `./mvnw test` | Validaciones del simulador |

## Si algo falla

La consola indica la causa probable de los errores de conexión. Los casos que pueden quedar:

| Situación | Solución |
|---|---|
| La API responde 503 y la consola dice "No hay conexión con Supabase" | En Supabase pulsa Resume project y vuelve a probar |
| La consola menciona el usuario o la contraseña | Revisa `DB_USER` (debe ser `postgres.<ref>`) y `DB_PASSWORD` en `.env` |
| `Could not resolve placeholder 'DB_URL'` | El archivo `.env` debe estar junto al `pom.xml` |
| El puerto 8080 está ocupado | Agrega `PORT=8081` al `.env` y cambia `baseUrl` en el entorno de Postman |

## Estructura del proyecto

```
perudeestacion/
  pom.xml, .env.example
  src/main/java/pe/upn/perudeestacion/
    config/        seguridad (JWT, CORS) y preparación automática de la base
    controller/    11 controladores REST
    dto/           datos de entrada (con validaciones) y de salida
    entity/        14 entidades JPA
    exception/     errores en formato ProblemDetail
    repository/    acceso a datos con Spring Data JPA
    security/      login, tokens y administrador inicial
    service/       reglas de negocio
  src/main/resources/db/   esquema y datos de prueba (01, 02 y 03)
  docs/pruebas/    datos en CSV, peticiones de ejemplo y script de reinicio
  docs/postman/    colección y entorno de pruebas
  docs/gestion/    backlog y plan del proyecto
  .github/         integración continua y tarea para mantener Supabase activo
```

## Plan gratuito de Supabase

Supabase pausa los proyectos gratuitos con poca actividad durante 7 días. Para evitarlo, el flujo "Mantener Supabase activo" hace dos consultas de lectura al día. Se activa creando el secreto `SUPABASE_DB_URL` en Settings, Secrets and variables, Actions, con la cadena URI del Session pooler (`postgresql://postgres.<ref>:<contraseña>@<host>:5432/postgres`).
