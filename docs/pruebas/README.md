# Datos para pruebas

Todo lo necesario para probar la API sin preparar nada a mano.

## Dónde están los datos

| Qué | Dónde | Para qué |
|---|---|---|
| Scripts que cargan la base | `src/main/resources/db/01_esquema.sql`, `02_datos_iniciales.sql` y `03_simulaciones_prueba.sql` | La aplicación los ejecuta sola si la base está vacía; también se pueden correr en el SQL Editor de Supabase en ese orden |
| Los mismos datos en CSV | `docs/pruebas/datos/` (13 archivos, uno por tabla) | Revisar los datos en Excel o en GitHub sin abrir la base |
| Cuerpos JSON de ejemplo | `docs/pruebas/peticiones/` (13 archivos) | Copiar y pegar en Postman o en el frontend |
| Colección de Postman | `docs/postman/PeruDeEstacion.postman_collection.json` | 38 peticiones con 69 aserciones |
| Entorno de Postman | `docs/postman/PeruDeEstacion.postman_environment.json` | URL base y usuario de prueba |

## Usuario de prueba

| Campo | Valor |
|---|---|
| Correo | `admin@perudeestacion.pe` |
| Contraseña | `CambiaEstaClave2026` |

La aplicación crea este usuario al arrancar con los valores `ADMIN_EMAIL` y `ADMIN_PASSWORD` del archivo `.env`; los de arriba son los de `.env.example`. Si los cambiaste en tu `.env`, usa los tuyos también en el entorno de Postman. La contraseña se guarda en la base solo como hash BCrypt.

## Contenido de los datos

| Archivo CSV | Registros | Qué contiene |
|---|---|---|
| 01_departamento | 25 | Los 24 departamentos y el Callao con su región natural |
| 02_alimento | 12 | Frutas, verduras, tubérculos y pescados |
| 03_temporada | 360 | Nivel ALTA, MEDIA o BAJA por mes en Piura, Tumbes, Lambayeque y Lima |
| 04_precio_referencia | 10 | Precios por kg en Piura y Lima |
| 05_consejo_conservacion | 5 | Cómo conservar cada alimento |
| 06_alerta_nino | 1 | Comunicado ENFEN 19-2026, vigente |
| 07_impacto_region | 3 | Impactos en Tumbes, Piura y Lambayeque |
| 08_impacto_alimento | 12 | Alimentos con riesgo de alza por región |
| 09_programa_apoyo | 5 | Programas del Estado con enlace oficial |
| 10_requisito | 10 | Requisitos de cada programa |
| 11_centro_apoyo | 4 | Centros de atención por departamento |
| 12_simulacion | 8 | Hogares simulados, calculados con la misma fórmula de la API |
| 13_parametro_simulador | 18 | Valores de la fórmula del simulador |

Los `id` de los CSV y de los ejemplos JSON corresponden a una base cargada desde cero con estos scripts (por ejemplo, Piura es el 20 y Mango el 1). Si tu base se cargó de otra forma, consulta los `id` con `GET /departamentos` y `GET /alimentos`.

## Cuerpos JSON de ejemplo

| Archivo | Petición | Respuesta esperada |
|---|---|---|
| 01_login.json | POST /auth/login | 200 con el token |
| 02_simulacion_valida.json | POST /simulaciones/racion | 201; 58 800 kcal y S/ 108,83 |
| 03_simulacion_invalida.json | POST /simulaciones/racion | 400; errores en dias y en el total de personas |
| 04_alimento_nuevo.json | POST /alimentos (con token) | 201; la segunda vez, 409 |
| 05_alimento_invalido.json | POST /alimentos (con token) | 400; errores en nombre, categoria e imagenUrl |
| 06_temporada_nueva.json | POST /temporadas (con token) | 201 |
| 07_temporada_duplicada.json | POST /temporadas (con token) | 409; Mango en Piura en noviembre ya existe |
| 08_precio_nuevo.json | POST /precios (con token) | 201 |
| 09_alerta_nueva_no_vigente.json | POST /alertas (con token) | 201; si se envía con vigente true, 409 |
| 10_impacto_nuevo.json | POST /impactos (con token) | 201 |
| 11_programa_nuevo.json | POST /programas-apoyo (con token) | 201 |
| 12_centro_nuevo.json | POST /centros-apoyo (con token) | 201 |
| 13_parametro_actualizado.json | PUT /parametros-simulador/KCAL_PERSONA_DIA (con token) | 200 |

"Con token" significa enviar la cabecera `Authorization: Bearer <token>` que devuelve el login.

## Volver a los datos iniciales

Para dejar la base de pruebas como al principio, ejecuta `docs/pruebas/reiniciar_datos.sql` en el SQL Editor de Supabase y reinicia la aplicación: volverá a crear las tablas y a cargar los datos. El script borra las 14 tablas del proyecto y nada más; úsalo solo en la base de pruebas.
