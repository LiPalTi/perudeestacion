-- =====================================================================
-- Peru de Estacion - Simulaciones de ejemplo (datos de prueba)
-- Ocho hogares de prueba calculados con la misma formula y parametros que
-- POST /api/v1/simulaciones/racion. Sirven para revisar la tabla simulacion
-- y el historial sin tener que llamar antes a la API.
-- La aplicacion ejecuta este script solo si la tabla simulacion esta vacia.
-- =====================================================================
INSERT INTO simulacion (departamento_id, adultos, ninos, adultos_mayores, dias, tipo_vivienda,
                        capacidad_economica, kcal_total, costo_estimado, creado_en) VALUES
((SELECT id FROM departamento WHERE codigo = 'PIU'), 4, 0, 0, 7, 'ADOBE', 'BAJA', 58800, 108.83, '2026-11-02 09:15:00'),
((SELECT id FROM departamento WHERE codigo = 'PIU'), 2, 3, 1, 10, 'QUINCHA', 'BAJA', 105000, 194.48, '2026-11-03 18:40:00'),
((SELECT id FROM departamento WHERE codigo = 'PIU'), 2, 1, 0, 5, 'MATERIAL_NOBLE', 'MEDIA', 28350, 52.50, '2026-11-05 12:05:00'),
((SELECT id FROM departamento WHERE codigo = 'TUM'), 3, 2, 0, 14, 'MADERA', 'BAJA', 129360, 239.66, '2026-11-04 08:30:00'),
((SELECT id FROM departamento WHERE codigo = 'TUM'), 1, 0, 1, 3, 'ESTERA', 'BAJA', 11970, 22.13, '2026-11-06 20:10:00'),
((SELECT id FROM departamento WHERE codigo = 'LAM'), 2, 2, 2, 7, 'ADOBE', 'MEDIA', 76440, 141.54, '2026-11-07 10:45:00'),
((SELECT id FROM departamento WHERE codigo = 'LAM'), 5, 0, 0, 30, 'MATERIAL_NOBLE', 'ALTA', 315000, 583.44, '2026-11-08 16:20:00'),
((SELECT id FROM departamento WHERE codigo = 'LIM'), 2, 1, 1, 7, 'MATERIAL_NOBLE', 'MEDIA', 52920, 98.05, '2026-11-10 11:00:00');
