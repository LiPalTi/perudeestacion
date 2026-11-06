# Plan del proyecto Perú de Estación

Proyecto final del curso Soluciones Web y Aplicaciones Distribuidas (8 semanas). La gestión combina las fases del PMI (inicio, planificación, ejecución, monitoreo y control, cierre) con Scrum para la ejecución en sprints, como se plantea en el curso.

## Roles Scrum

| Rol | Responsable | Función en el proyecto |
|---|---|---|
| Product Owner | Lizetty Rebeca Palma Tito | Prioriza el Product Backlog y acepta cada historia contra sus criterios |
| Scrum Master | Lizetty Rebeca Palma Tito | Organiza los eventos y retira impedimentos |
| Equipo de desarrollo | Lizetty Rebeca Palma Tito | Backend, base de datos, frontend y pruebas |
| Interesado | Docente Hanz Paul Cocchi Guerrero | Revisa los incrementos en cada entrega |

Si el proyecto es individual, la misma persona asume los tres roles. Se deja constancia en el informe.

## Fases PMI y sprints

| Semana | Fase PMI | Sprint | Objetivo (Sprint Goal) | Entregable |
|---|---|---|---|---|
| 1 | Inicio | Sprint 0 | Definir problema, alcance y requerimientos | Informe inicial |
| 2 | Planificación | Sprint 0 | Backlog priorizado, modelo de datos y arquitectura | Backlog en Jira/Trello, diagramas en Draw.io |
| 2 a 3 | Ejecución | Sprint 1 | API REST funcional con persistencia en Supabase, CRUD, validaciones, JWT y pruebas | Backend + informe técnico + colección Postman |
| 4 a 5 | Ejecución | Sprint 2 | Consulta de temporadas desde Angular | Pantallas de consulta y ficha |
| 6 a 7 | Ejecución, monitoreo y control | Sprint 3 | Módulo El Niño, simulador y panel admin en Angular; pruebas Selenium | Aplicación integrada |
| 8 | Cierre | Cierre | Despliegue, informe final y sustentación | Producto final |

## Eventos por sprint

| Evento | Duración sugerida | Evidencia que se guarda |
|---|---|---|
| Sprint Planning | 30 min | Captura del sprint en Jira o Trello con las historias seleccionadas |
| Daily | 10 min (o nota diaria si es individual) | Comentarios en la tarjeta o issue |
| Sprint Review | 20 min | Demostración con Postman o la web; captura del Runner |
| Retrospectiva | 15 min | Tabla "qué salió bien / qué mejorar / acción" en el informe |

## Definition of Done

Una historia está terminada cuando cumple todo lo siguiente:

1. El endpoint responde según sus criterios de aceptación.
2. Los datos persisten en Supabase.
3. Las entradas inválidas devuelven 400 con detalle por campo.
4. Las operaciones de escritura exigen token de administrador.
5. Existe al menos una prueba en Postman en verde.
6. El código está subido a GitHub con un commit que menciona la clave de la historia (por ejemplo "HU01 consulta por departamento").

## Velocidad planificada

| Sprint | Story points | Estado |
|---|---|---|
| Sprint 1 (backend) | 57 | En curso |
| Sprint 2 | 13 | Por hacer |
| Sprint 3 | 19 | Por hacer |
| Cierre | 3 | Por hacer |

## Riesgos

| Riesgo | Probabilidad | Impacto | Respuesta |
|---|---|---|---|
| Supabase pausa el proyecto gratuito tras 7 días sin uso | Alta | Medio | Entrar al panel antes de cada revisión y pulsar Restore si aparece pausado |
| La red de la universidad bloquea el puerto 5432 | Media | Alto | Probar desde datos móviles; usar el pooler en modo transacción (puerto 6543) |
| Precios de prueba no reales | Alta | Medio | Reemplazar con datos de SISAP-MIDAGRI antes del cierre, desde el CRUD de precios |
| Cambios en la alerta ENFEN | Alta | Bajo | Actualizar la alerta por el CRUD; el siguiente comunicado del ENFEN se esperaba para el 13 de noviembre de 2026 |
| Falta de tiempo en el frontend | Media | Alto | Priorizar las historias Must y dejar las Could para el final |
