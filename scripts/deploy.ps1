# Script de despliegue para PizzaHub API
# Este script automatiza el proceso de commit y push a GitHub

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  PizzaHub API - Script de Despliegue  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar si hay cambios
Write-Host "Verificando cambios..." -ForegroundColor Yellow
git status

Write-Host ""
Write-Host "¿Deseas continuar con el commit y push? (S/N): " -ForegroundColor Green -NoNewline
$respuesta = Read-Host

if ($respuesta -eq "S" -or $respuesta -eq "s") {
    # Agregar todos los archivos
    Write-Host ""
    Write-Host "Agregando archivos..." -ForegroundColor Yellow
    git add .
    
    # Mensaje de commit
    Write-Host ""
    Write-Host "Ingresa el mensaje de commit (Enter para usar el predeterminado): " -ForegroundColor Green -NoNewline
    $mensaje = Read-Host
    
    if ([string]::IsNullOrWhiteSpace($mensaje)) {
        $mensaje = "Fix: Corregir tipo de dato UnidadMedida y configuración CORS para Insumos"
    }
    
    # Hacer commit
    Write-Host ""
    Write-Host "Realizando commit..." -ForegroundColor Yellow
    git commit -m "$mensaje"
    
    # Hacer push
    Write-Host ""
    Write-Host "Enviando cambios a GitHub..." -ForegroundColor Yellow
    git push origin back-end-structure
    
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  ✓ Despliegue completado exitosamente  " -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Próximos pasos:" -ForegroundColor Cyan
    Write-Host "1. Render detectará los cambios automáticamente" -ForegroundColor White
    Write-Host "2. Espera 5-10 minutos para que se complete el despliegue" -ForegroundColor White
    Write-Host "3. Accede a: https://pizzahub-api.onrender.com/migrate" -ForegroundColor White
    Write-Host "4. Verifica con: https://pizzahub-api.onrender.com/health" -ForegroundColor White
    Write-Host "5. Prueba el API con Swagger: https://pizzahub-api.onrender.com/swagger" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "Operación cancelada." -ForegroundColor Red
    Write-Host ""
}

Write-Host "Presiona cualquier tecla para salir..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
