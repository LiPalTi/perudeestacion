-- Peru de Estacion - Reiniciar la base de PRUEBAS
-- Borra las 14 tablas del proyecto (y solo esas). Al reiniciar la aplicacion,
-- esta vuelve a crear las tablas y a cargar los datos de prueba.
DROP TABLE IF EXISTS
    impacto_alimento, impacto_region, alerta_nino,
    requisito, centro_apoyo, programa_apoyo,
    simulacion, parametro_simulador, usuario_admin,
    consejo_conservacion, precio_referencia, temporada,
    alimento, departamento
CASCADE;
