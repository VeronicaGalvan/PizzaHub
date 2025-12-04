# 🔧 Fix: Error 500 en POST /api/Insumos

## 📋 Problema Identificado

La base de datos en **producción** tiene la columna `unidad_medida` de la tabla `insumos` como tipo **INTEGER**, mientras que el modelo en C# espera un **VARCHAR(10)**.

### Error Original:
```
column "unidad_medida" is of type integer but expression is of type character varying
```

## ✅ Solución Implementada

Se creó una migración para cambiar el tipo de dato de `unidad_medida` de `integer` a `varchar(10)`.

### Archivos Modificados:
1. `Migrations/20251204013200_FixUnidadMedidaType.cs` - Nueva migración
2. `Migrations/20251204013200_FixUnidadMedidaType.Designer.cs` - Designer de la migración
3. `Migrations/PizzaHubContextModelSnapshot.cs` - Actualización del snapshot
4. `Program.cs` - Nuevo endpoint `/fix-unidad-medida`
5. `scripts/fix_unidad_medida.sql` - Script SQL manual (backup)

## 🚀 Pasos para Aplicar el Fix en Producción

### Opción 1: Usar el Endpoint Automático (Recomendado si NO hay datos)

1. **Espera 5-10 minutos** para que Render despliegue los cambios

2. **Verifica que no haya insumos** en la base de datos:
   ```bash
   curl https://pizzahub-api.onrender.com/api/Insumos
   ```

3. **Si la tabla está vacía**, ejecuta el endpoint de fix:
   ```bash
   curl https://pizzahub-api.onrender.com/fix-unidad-medida
   ```

4. **Verifica el health check**:
   ```bash
   curl https://pizzahub-api.onrender.com/health
   ```

5. **Aplica las migraciones** (esto ejecutará la migración FixUnidadMedidaType):
   ```bash
   curl https://pizzahub-api.onrender.com/migrate
   ```

6. **Prueba crear un insumo**:
   ```bash
   curl -X 'POST' \
     'https://pizzahub-api.onrender.com/api/Insumos' \
     -H 'Authorization: Bearer [TU-TOKEN]' \
     -H 'Content-Type: application/json' \
     -d '{
     "nombre": "Harina",
     "unidadMedida": "kg",
     "stockInicial": 12,
     "stockMinimo": 5
   }'
   ```

### Opción 2: Usar Script SQL Manual (Si hay datos importantes)

Si ya tienes insumos registrados con valores numéricos en `unidad_medida`:

1. **Accede a tu base de datos PostgreSQL en Render**:
   - Ve a tu dashboard de Render
   - Selecciona tu base de datos PostgreSQL
   - Haz clic en "Connect" > "PSQL Command"

2. **Ejecuta el script SQL** ubicado en `scripts/fix_unidad_medida.sql`:
   ```sql
   -- Opción B del script: Migración con datos existentes
   ALTER TABLE insumos ADD COLUMN unidad_medida_temp VARCHAR(10);
   
   UPDATE insumos SET unidad_medida_temp = 
       CASE 
           WHEN unidad_medida = 1 THEN 'kg'
           WHEN unidad_medida = 2 THEN 'g'
           WHEN unidad_medida = 3 THEN 'L'
           WHEN unidad_medida = 4 THEN 'ml'
           WHEN unidad_medida = 5 THEN 'Uds'
           ELSE 'Uds'
       END;
   
   ALTER TABLE insumos DROP COLUMN unidad_medida;
   ALTER TABLE insumos RENAME COLUMN unidad_medida_temp TO unidad_medida;
   ALTER TABLE insumos ALTER COLUMN unidad_medida SET NOT NULL;
   ```

3. **Verifica el cambio**:
   ```sql
   SELECT column_name, data_type, character_maximum_length
   FROM information_schema.columns
   WHERE table_name = 'insumos' AND column_name = 'unidad_medida';
   ```

### Opción 3: Eliminar Datos y Aplicar Migración (Más Simple)

Si los datos actuales no son importantes:

1. **Elimina todos los insumos**:
   ```sql
   DELETE FROM inventario_log WHERE insumo_id IN (SELECT id FROM insumos);
   DELETE FROM insumos;
   ```

2. **Aplica el fix automático**:
   ```bash
   curl https://pizzahub-api.onrender.com/fix-unidad-medida
   ```

3. **Aplica migraciones**:
   ```bash
   curl https://pizzahub-api.onrender.com/migrate
   ```

## 📝 Valores Válidos para unidad_medida

Ahora `unidadMedida` debe ser un **string** con valores descriptivos:

- `"kg"` - Kilogramos
- `"g"` - Gramos
- `"L"` - Litros
- `"ml"` - Mililitros
- `"Uds"` - Unidades
- `"pza"` - Piezas
- `"lt"` - Litros (alternativo)

## ✅ Verificación Final

Una vez aplicado el fix, tu curl debería funcionar correctamente:

```bash
curl -X 'POST' \
  'https://pizzahub-api.onrender.com/api/Insumos' \
  -H 'Authorization: Bearer [TU-TOKEN]' \
  -H 'Content-Type: application/json' \
  -d '{
  "nombre": "Harina",
  "unidadMedida": "kg",
  "stockInicial": 12,
  "stockMinimo": 5
}'
```

**Respuesta esperada** (201 Created):
```json
{
  "id": 1,
  "nombre": "Harina",
  "unidadMedida": "kg",
  "stockActual": 12,
  "stockMinimo": 5,
  "ultimaActualizacion": "2025-12-04T01:45:00Z"
}
```

## 🔍 Troubleshooting

### Si el error persiste:

1. **Verifica el tipo de dato actual**:
   ```sql
   SELECT column_name, data_type 
   FROM information_schema.columns 
   WHERE table_name = 'insumos' AND column_name = 'unidad_medida';
   ```

2. **Revisa los logs de Render**:
   - Ve a tu servicio en Render
   - Haz clic en "Logs"
   - Busca errores relacionados con migraciones

3. **Contacta si necesitas ayuda** con los siguientes datos:
   - Resultado del query de verificación del tipo de dato
   - Logs de error de Render
   - Resultado de `/health` y `/config-check`

---

**Fecha de creación:** 2025-12-03  
**Estado:** ✅ Cambios desplegados - Pendiente aplicar fix en producción
