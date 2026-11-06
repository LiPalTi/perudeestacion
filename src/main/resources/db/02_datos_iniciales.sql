-- =====================================================================
-- Peru de Estacion - Datos iniciales
-- Ejecutar DESPUES de 01_esquema.sql, en Supabase > SQL Editor.
-- IMPORTANTE: las temporadas, precios y centros marcados como "prueba" son datos
-- de ejemplo para probar la API. Reemplazarlos con fuentes oficiales
-- (calendario de cosecha MIDAGRI, SISAP) antes de la entrega final.
-- =====================================================================

-- 24 departamentos + Provincia Constitucional del Callao (region natural predominante)
INSERT INTO departamento (codigo, nombre, region_natural) VALUES
('AMA','Amazonas','SELVA'), ('ANC','Ancash','SIERRA'), ('APU','Apurimac','SIERRA'),
('ARE','Arequipa','SIERRA'), ('AYA','Ayacucho','SIERRA'), ('CAJ','Cajamarca','SIERRA'),
('CAL','Callao','COSTA'), ('CUS','Cusco','SIERRA'), ('HUV','Huancavelica','SIERRA'),
('HUC','Huanuco','SIERRA'), ('ICA','Ica','COSTA'), ('JUN','Junin','SIERRA'),
('LAL','La Libertad','COSTA'), ('LAM','Lambayeque','COSTA'), ('LIM','Lima','COSTA'),
('LOR','Loreto','SELVA'), ('MDD','Madre de Dios','SELVA'), ('MOQ','Moquegua','SIERRA'),
('PAS','Pasco','SIERRA'), ('PIU','Piura','COSTA'), ('PUN','Puno','SIERRA'),
('SAM','San Martin','SELVA'), ('TAC','Tacna','COSTA'), ('TUM','Tumbes','COSTA'),
('UCA','Ucayali','SELVA');

INSERT INTO alimento (nombre, categoria, descripcion) VALUES
('Mango','FRUTA','Fruta tropical de la costa norte.'),
('Limon','FRUTA','Citrico de uso diario; sensible a las lluvias en la costa norte.'),
('Platano','FRUTA','Fruta de alta disponibilidad en costa norte y selva.'),
('Uva','FRUTA','Fruta de la costa sur y central.'),
('Tomate','VERDURA','Hortaliza de alta rotacion en mercados.'),
('Cebolla roja','VERDURA','Hortaliza basica de la canasta familiar.'),
('Zanahoria','VERDURA','Hortaliza de raiz.'),
('Papa amarilla','TUBERCULO','Tuberculo andino.'),
('Camote','TUBERCULO','Tuberculo de costa.'),
('Yuca','TUBERCULO','Raiz de selva y costa norte.'),
('Bonito','PESCADO','Pescado azul del litoral.'),
('Perico','PESCADO','Pesca artesanal de la costa norte.');

-- Temporadas de PRUEBA (Piura, Tumbes, Lambayeque y Lima) generadas por meses.
INSERT INTO temporada (alimento_id, departamento_id, mes, nivel)
SELECT a.id, d.id, m.mes,
       CASE WHEN m.mes = ANY (x.alta) THEN 'ALTA' WHEN m.mes = ANY (x.media) THEN 'MEDIA' ELSE 'BAJA' END
FROM (VALUES
        ('Mango',        ARRAY[11,12,1,2],  ARRAY[3,10]),
        ('Limon',        ARRAY[1,2,3,4],    ARRAY[5,6,12]),
        ('Platano',      ARRAY[1,2,3,4,5,6,7,8,9,10,11,12], ARRAY[]::int[]),
        ('Tomate',       ARRAY[6,7,8],      ARRAY[5,9]),
        ('Cebolla roja', ARRAY[8,9,10],     ARRAY[7,11]),
        ('Camote',       ARRAY[5,6,7],      ARRAY[4,8,9]),
        ('Yuca',         ARRAY[3,4,5,6],    ARRAY[2,7]),
        ('Perico',       ARRAY[12,1,2],     ARRAY[11,3])
     ) AS x (alimento, alta, media)
JOIN alimento a ON a.nombre = x.alimento
CROSS JOIN (SELECT id FROM departamento WHERE codigo IN ('PIU','TUM','LAM')) d
CROSS JOIN generate_series(1, 12) AS m (mes);

INSERT INTO temporada (alimento_id, departamento_id, mes, nivel)
SELECT a.id, d.id, m.mes,
       CASE WHEN m.mes = ANY (x.alta) THEN 'ALTA' WHEN m.mes = ANY (x.media) THEN 'MEDIA' ELSE 'BAJA' END
FROM (VALUES
        ('Uva',           ARRAY[12,1,2,3], ARRAY[4,11]),
        ('Tomate',        ARRAY[6,7,8,9],  ARRAY[5,10]),
        ('Zanahoria',     ARRAY[5,6,7],    ARRAY[4,8]),
        ('Papa amarilla', ARRAY[4,5,6],    ARRAY[3,7]),
        ('Camote',        ARRAY[5,6,7,8],  ARRAY[4,9]),
        ('Bonito',        ARRAY[10,11,12], ARRAY[9,1])
     ) AS x (alimento, alta, media)
JOIN alimento a ON a.nombre = x.alimento
CROSS JOIN (SELECT id FROM departamento WHERE codigo = 'LIM') d
CROSS JOIN generate_series(1, 12) AS m (mes);

-- Precios de PRUEBA (reemplazar con SISAP-MIDAGRI indicando la fecha real).
INSERT INTO precio_referencia (alimento_id, departamento_id, precio_kg, fecha, fuente)
SELECT a.id, d.id, x.precio, DATE '2026-10-20', 'Dato de prueba (reemplazar con SISAP-MIDAGRI)'
FROM (VALUES ('Mango','PIU',3.50), ('Limon','PIU',6.00), ('Platano','PIU',2.20), ('Tomate','PIU',3.80),
             ('Cebolla roja','PIU',3.20), ('Perico','PIU',14.00),
             ('Tomate','LIM',4.10), ('Papa amarilla','LIM',4.60), ('Bonito','LIM',12.50), ('Uva','LIM',6.50)
     ) AS x (alimento, dep, precio)
JOIN alimento a ON a.nombre = x.alimento
JOIN departamento d ON d.codigo = x.dep;

INSERT INTO consejo_conservacion (alimento_id, metodo, descripcion, duracion_dias)
SELECT a.id, x.metodo, x.descripcion, x.dias
FROM (VALUES
        ('Mango','Maduracion a temperatura ambiente','Madurar fuera de la refrigeradora y refrigerar recien maduro.',5),
        ('Limon','Refrigeracion','Guardar en bolsa cerrada dentro de la refrigeradora.',21),
        ('Tomate','Lugar fresco','Conservar fuera de la refrigeradora hasta madurar, sin apilar.',7),
        ('Papa amarilla','Lugar oscuro y ventilado','Evitar la luz para que no se ponga verde; no refrigerar.',30),
        ('Bonito','Congelacion','Limpiar, porcionar y congelar el mismo dia de la compra.',90)
     ) AS x (alimento, metodo, descripcion, dias)
JOIN alimento a ON a.nombre = x.alimento;

-- Alerta vigente segun el Comunicado Oficial ENFEN N.° 19-2026 (30-oct-2026).
INSERT INTO alerta_nino (comunicado, fecha, estado, magnitud, vigente)
VALUES ('ENFEN 19-2026', DATE '2026-10-30', 'Alerta de El Nino Costero', 'Extraordinaria', TRUE);

INSERT INTO impacto_region (alerta_id, departamento_id, sector, nivel_riesgo, descripcion)
SELECT al.id, d.id, 'Agricultura', 'ALTO',
       'Lluvias sobre lo normal y caudales altos previstos de noviembre 2026 a enero 2027; riesgo de perdida de cultivos y cortes de vias.'
FROM alerta_nino al CROSS JOIN departamento d
WHERE al.vigente AND d.codigo IN ('TUM','PIU','LAM');

INSERT INTO impacto_alimento (impacto_id, alimento_id, riesgo_alza)
SELECT i.id, a.id, x.riesgo
FROM impacto_region i
CROSS JOIN (VALUES ('Limon','ALTO'), ('Tomate','ALTO'), ('Cebolla roja','MEDIO'), ('Mango','MEDIO')) AS x (alimento, riesgo)
JOIN alimento a ON a.nombre = x.alimento;

-- Programas de apoyo y requisitos (verificar en la URL oficial antes de cada entrega).
INSERT INTO programa_apoyo (nombre, entidad, url_oficial, fecha_revision) VALUES
('Clasificacion socioeconomica (SISFOH)','MIDIS - Municipalidad (ULE)','https://www.gob.pe/445-sistema-de-focalizacion-de-hogares-sisfoh-solicitar-la-clasificacion-socioeconomica-de-mi-hogar', DATE '2026-11-04'),
('Ayuda humanitaria','INDECI - Municipalidad distrital','https://www.gob.pe/indeci', DATE '2026-11-04'),
('Bono de Arrendamiento para Emergencias (BAE)','Ministerio de Vivienda','https://www.gob.pe/vivienda', DATE '2026-11-04'),
('Comedores populares (PCA)','MIDIS - Municipalidad','https://www.gob.pe/midis', DATE '2026-11-04'),
('Ollas comunes (PCA)','MIDIS - Municipalidad','https://www.gob.pe/midis', DATE '2026-11-04');

INSERT INTO requisito (programa_id, descripcion, orden)
SELECT p.id, x.descripcion, x.orden
FROM (VALUES
        ('Clasificacion socioeconomica (SISFOH)','DNI de todos los integrantes del hogar',1),
        ('Clasificacion socioeconomica (SISFOH)','Ultimo recibo de luz',2),
        ('Clasificacion socioeconomica (SISFOH)','Formulario S100 llenado en la ULE',3),
        ('Ayuda humanitaria','Estar empadronado como damnificado o afectado tras la evaluacion de danos de la municipalidad',1),
        ('Bono de Arrendamiento para Emergencias (BAE)','Vivienda colapsada o inhabitable registrada en el padron de damnificados',1),
        ('Bono de Arrendamiento para Emergencias (BAE)','No tener otra vivienda en la region',2),
        ('Bono de Arrendamiento para Emergencias (BAE)','Residir en un distrito declarado en emergencia',3),
        ('Comedores populares (PCA)','Acudir a un comedor reconocido por la municipalidad del distrito',1),
        ('Ollas comunes (PCA)','Olla comun con un minimo de 15 usuarios',1),
        ('Ollas comunes (PCA)','Los usuarios no deben recibir otro servicio del PCA',2)
     ) AS x (programa, descripcion, orden)
JOIN programa_apoyo p ON p.nombre = x.programa;

-- Centros de PRUEBA: verificar direccion y telefono reales en cada municipalidad.
INSERT INTO centro_apoyo (programa_id, departamento_id, nombre, direccion, distrito, telefono)
SELECT p.id, d.id, x.nombre, x.direccion, x.distrito, NULL
FROM (VALUES
        ('Clasificacion socioeconomica (SISFOH)','PIU','ULE - Municipalidad Provincial de Piura','Sede de la Municipalidad Provincial de Piura','Piura'),
        ('Clasificacion socioeconomica (SISFOH)','TUM','ULE - Municipalidad Provincial de Tumbes','Sede de la Municipalidad Provincial de Tumbes','Tumbes'),
        ('Clasificacion socioeconomica (SISFOH)','LAM','ULE - Municipalidad Provincial de Chiclayo','Sede de la Municipalidad Provincial de Chiclayo','Chiclayo'),
        ('Ayuda humanitaria','PIU','Oficina de Gestion del Riesgo de Desastres - Piura','Sede de la Municipalidad Provincial de Piura','Piura')
     ) AS x (programa, dep, nombre, direccion, distrito)
JOIN programa_apoyo p ON p.nombre = x.programa
JOIN departamento d ON d.codigo = x.dep;

-- Parametros del simulador (editables con PUT /api/v1/parametros-simulador/{clave}).
INSERT INTO parametro_simulador (clave, valor, descripcion) VALUES
('KCAL_PERSONA_DIA', 2100, 'Energia minima por persona y dia (PMA/WFP)'),
('FACTOR_NINO', 0.70, 'Factor preliminar para ninos, a validar con nutricionista'),
('FACTOR_ADULTO_MAYOR', 0.90, 'Factor preliminar para adultos mayores, a validar con nutricionista'),
('P_CEREALES_TUBERCULOS', 0.55, 'Proporcion de energia: cereales y tuberculos'),
('P_MENESTRAS', 0.15, 'Proporcion de energia: menestras'),
('P_ACEITE', 0.15, 'Proporcion de energia: aceite vegetal'),
('P_CONSERVAS_PESCADO', 0.10, 'Proporcion de energia: conservas de pescado'),
('P_AZUCAR', 0.05, 'Proporcion de energia: azucar'),
('KCAL_KG_CEREALES_TUBERCULOS', 3500, 'kcal por kg (referencial, validar con Tablas Peruanas INS)'),
('KCAL_KG_MENESTRAS', 3400, 'kcal por kg (referencial, validar con Tablas Peruanas INS)'),
('KCAL_KG_ACEITE', 8840, 'kcal por kg (referencial, validar con Tablas Peruanas INS)'),
('KCAL_KG_CONSERVAS_PESCADO', 1900, 'kcal por kg (referencial, validar con Tablas Peruanas INS)'),
('KCAL_KG_AZUCAR', 3870, 'kcal por kg (referencial, validar con Tablas Peruanas INS)'),
('PRECIO_KG_CEREALES_TUBERCULOS', 4.50, 'Precio referencial S/ por kg (actualizar)'),
('PRECIO_KG_MENESTRAS', 7.00, 'Precio referencial S/ por kg (actualizar)'),
('PRECIO_KG_ACEITE', 9.00, 'Precio referencial S/ por kg (actualizar)'),
('PRECIO_KG_CONSERVAS_PESCADO', 12.00, 'Precio referencial S/ por kg (actualizar)'),
('PRECIO_KG_AZUCAR', 4.00, 'Precio referencial S/ por kg (actualizar)');

-- El usuario administrador NO se inserta aqui: lo crea la aplicacion al arrancar
-- con ADMIN_EMAIL y ADMIN_PASSWORD del archivo .env (la contrasena se guarda con BCrypt).
