-- Script para actualizar la base de datos de Insumos en PostgreSQL
-- Ejecutar este script si la base de datos YA TIENE DATOS y no quieres recrearla

-- Paso 1: Verificar la estructura actual
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'insumos' AND column_name = 'unidad_medida';

-- Paso 2: Verificar si hay datos en la tabla
SELECT COUNT(*) as total_insumos FROM insumos;

-- Paso 3: Si hay datos, convertirlos primero
-- (Solo ejecutar este paso si hay datos y unidad_medida es tipo integer)
UPDATE insumos 
SET unidad_medida = CASE 
    WHEN unidad_medida::integer = 0 THEN 'Kg'
    WHEN unidad_medida::integer = 1 THEN 'g'
    WHEN unidad_medida::integer = 2 THEN 'L'
    WHEN unidad_medida::integer = 3 THEN 'ml'
    WHEN unidad_medida::integer = 4 THEN 'Uds'
    ELSE 'Uds'
END;

-- Paso 4: Cambiar el tipo de columna
ALTER TABLE insumos 
ALTER COLUMN unidad_medida TYPE character varying(10);

-- Paso 5: Hacer la columna NOT NULL si aún no lo es
ALTER TABLE insumos 
ALTER COLUMN unidad_medida SET NOT NULL;

-- Paso 6: Verificar el cambio
SELECT column_name, data_type, character_maximum_length, is_nullable
FROM information_schema.columns 
WHERE table_name = 'insumos' AND column_name = 'unidad_medida';

-- Paso 7: Verificar los datos actuales
SELECT id, nombre, unidad_medida, stock_actual, stock_minimo 
FROM insumos;
