# Perú de Estación: frontend Angular

Interfaz web en Angular 22 con Bootstrap 5.3 que consume la API Spring Boot de este repositorio.

## Ejecutar

Necesitas Node.js 22 o superior y la API corriendo en `http://localhost:8080`.

```
cd frontend
npm install
npm start
```

Abre `http://localhost:4200`. La dirección de la API está en `src/environments/environment.ts`; cámbiala ahí si la API usa otro puerto.

## Pantallas

| Ruta | Pantalla | Qué consume de la API |
|---|---|---|
| `/` | Temporada por departamento y mes, con filtro por categoría | `GET /departamentos`, `GET /temporadas` |
| `/alimentos/:id` | Ficha del alimento: precio, calendario de 12 meses y conservación | `GET /alimentos/{id}`, `GET /alimentos/{id}/calendario` |
| `/el-nino` | Alerta vigente e impactos en Tumbes, Piura y Lambayeque | `GET /alertas`, `GET /departamentos/{codigo}` |
| `/simulador` | Simulador de ración mínima con validaciones | `POST /simulaciones/racion` |
| `/apoyo` | Programas del Estado y centros de atención | `GET /programas-apoyo`, `GET /centros-apoyo` |
| `/login` | Ingreso del administrador | `POST /auth/login` (devuelve el JWT) |
| `/admin/alimentos` | CRUD de alimentos (protegido) | `GET`, `POST`, `PUT`, `DELETE /alimentos` |
| `/admin/temporadas` | CRUD de temporadas (protegido) | `GET`, `POST`, `PUT`, `DELETE /temporadas` |

Usuario de prueba: `admin@perudeestacion.pe` con la contraseña `ADMIN_PASSWORD` del `.env` del backend (en `.env.example`, `CambiaEstaClave2026`).

## Cómo funciona la autenticación

| Pieza | Archivo | Qué hace |
|---|---|---|
| Servicio de sesión | `src/app/core/auth.service.ts` | Llama al login y guarda el token en `sessionStorage` (se borra al cerrar la pestaña) |
| Interceptor de token | `src/app/core/interceptores.ts` | Agrega `Authorization: Bearer <token>` a cada petición |
| Interceptor de errores | `src/app/core/interceptores.ts` | Si la API responde 401, cierra la sesión y lleva al login; si no responde o responde 503, muestra un aviso |
| Guardia de rutas | `src/app/core/auth.guard.ts` | Sin sesión, `/admin` redirige a `/login` y luego vuelve a la página pedida |

## Validaciones

Los formularios usan las mismas reglas que el backend, para avisar antes de enviar. Si la API responde 400, los mensajes por campo (`errores`) se muestran debajo de cada campo; si responde 409 (por ejemplo, un nombre repetido), el mensaje aparece sobre el botón Guardar.

## Imágenes: cómo subirlas o cambiarlas

Hay tres formas, de la más sencilla a la más técnica:

| Opción | Qué hacer | Cuándo usarla |
|---|---|---|
| 1. Archivo en la carpeta | Copia la imagen en `public/img/alimentos/` con el nombre del alimento | Para fotos propias que van dentro del proyecto |
| 2. Enlace desde el panel | Ingresa a Administración, Alimentos, Editar, y pega el enlace en "URL de imagen" | Para cambiar una imagen sin tocar código; se guarda en la base |
| 3. Imagen de portada | Copia una foto como `public/img/portada.webp` | Para el fondo verde de la página principal |

**Nombre del archivo (opción 1):** el nombre del alimento en minúsculas, sin tildes y con guiones en lugar de espacios. Se acepta `.webp`, `.jpg` o `.png`.

| Alimento | Archivo |
|---|---|
| Mango | `mango.webp` (incluido como ejemplo) |
| Plátano | `platano.jpg` |
| Cebolla roja | `cebolla-roja.webp` |
| Papa amarilla | `papa-amarilla.png` |

Recomendado: imagen cuadrada de unos 600 x 600 px y menos de 150 KB. Para ver el cambio basta con recargar la página (con `npm start` corriendo).

**Orden en que se busca la imagen:** primero el enlace guardado en el alimento; si no tiene o no carga, el archivo de la carpeta; si tampoco existe, el dibujo gris "Agrega tu imagen" (`public/img/sin-imagen.svg`). Así ninguna tarjeta queda rota.

**Otros cambios visuales:** los colores de toda la aplicación están al inicio de `src/styles.css` (variables `--pe-verde`, `--pe-naranja`, etc.).

## Pruebas de integración

Las pruebas de `e2e/` abren Chrome, usan la aplicación como un usuario y verifican lo que responde la API real.

| Archivo | Qué prueba |
|---|---|
| `01_publico.test.mjs` | Consulta Piura en noviembre (Mango y alerta de El Niño), filtro por categoría, ficha con 12 meses, El Niño, validaciones del simulador y cálculo de 4 adultos por 7 días (58 800 kcal, S/ 108.83) |
| `02_admin.test.mjs` | Ruta protegida sin sesión, login fallido (401) y correcto, cierre de sesión, y CRUD completo de alimentos y temporadas con validaciones, duplicados (409) y eliminación con confirmación |

Se ejecutan solas en GitHub Actions en cada cambio. Para correrlas en tu equipo, con la API y `npm start` levantados y Google Chrome instalado:

```
npm run test:e2e
```

Si tu contraseña de administrador no es la de `.env.example`, antes define `ADMIN_PASSWORD` (en Windows PowerShell: `$env:ADMIN_PASSWORD="tu-clave"`).

## Estructura

```
frontend/
  src/app/core/      servicios de la API y de sesión, interceptores, guardia y modelos
  src/app/shared/    imagen de alimento y etiqueta de nivel
  src/app/pages/     una carpeta por pantalla (admin incluye alimentos y temporadas)
  src/styles.css     colores y estilos generales
  public/img/        imágenes (alimentos/, sin-imagen.svg, portada opcional)
  e2e/               pruebas de integración con Selenium
```
