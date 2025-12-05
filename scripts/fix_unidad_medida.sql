-- Script para corregir el tipo de dato de unidad_medida en la tabla insumos
-- Este script debe ejecutarse en la base de datos de producción

-- Paso 1: Verificar el tipo actual
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns
WHERE table_name = 'insumos' AND column_name = 'unidad_medida';

-- Paso 2: Si la columna es integer, cambiarla a varchar
-- Primero, verificar si hay datos en la tabla
SELECT COUNT(*) as total_insumos FROM insumos;

-- Paso 3: Si hay datos, necesitamos hacer una conversión segura
-- Opción A: Si NO hay datos importantes, simplemente cambiar el tipo
ALTER TABLE insumos ALTER COLUMN unidad_medida TYPE VARCHAR(10);

-- Opción B: Si HAY datos, crear una nueva columna temporal y migrar
-- (Comentado por defecto, descomentar si es necesario)
/*
-- Agregar nueva columna temporal
ALTER TABLE insumos ADD COLUMN unidad_medida_temp VARCHAR(10);

-- Migrar datos: convertir números a texto de unidades
UPDATE insumos SET unidad_medida_temp = 
    CASE 
        WHEN unidad_medida = 1 THEN 'kg'
        WHEN unidad_medida = 2 THEN 'g'
        WHEN unidad_medida = 3 THEN 'L'
        WHEN unidad_medida = 4 THEN 'ml'
        WHEN unidad_medida = 5 THEN 'Uds'
        ELSE 'Uds'
    END;

-- Eliminar columna antigua
ALTER TABLE insumos DROP COLUMN unidad_medida;

-- Renombrar columna temporal
ALTER TABLE insumos RENAME COLUMN unidad_medida_temp TO unidad_medida;

-- Establecer como NOT NULL
ALTER TABLE insumos ALTER COLUMN unidad_medida SET NOT NULL;
*/

-- Paso 4: Verificar el cambio
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns
WHERE table_name = 'insumos' AND column_name = 'unidad_medida';

-- Paso 5: Mostrar datos actuales
SELECT id, nombre, unidad_medida, stock_actual FROM insumos;
