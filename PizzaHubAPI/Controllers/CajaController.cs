using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;
using System.Text.Json;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
[Authorize]
public class CajaController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public CajaController(PizzaHubContext context)
    {
        _context = context;
    }

    // GET: api/Caja
    [HttpGet]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<IEnumerable<Caja>>> GetCajas()
    {
        return await _context.Cajas
            .Include(c => c.Empleado)
            .OrderByDescending(c => c.Fecha)
            .ToListAsync();
    }

    // GET: api/Caja/5
    [HttpGet("{id}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<Caja>> GetCaja(int id)
    {
        var caja = await _context.Cajas
            .Include(c => c.Empleado)
            .Include(c => c.Ventas)
            .FirstOrDefaultAsync(c => c.Id == id);

        if (caja == null)
        {
            return NotFound(new { message = "Caja no encontrada" });
        }

        return caja;
    }

    // GET: api/Caja/abierta
    [HttpGet("abierta")]
    public async Task<ActionResult<Caja>> GetCajaAbierta()
    {
        var caja = await _context.Cajas
            .Include(c => c.Empleado)
            .FirstOrDefaultAsync(c => c.Estado == EstadoCajaEnum.Abierta);

        if (caja == null)
        {
            return NotFound(new { message = "No hay caja abierta" });
        }

        return caja;
    }

    // POST: api/Caja/abrir
    // Recibimos el body como JsonElement para no tocar DTOs ni modelos
    [HttpPost("abrir")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<Caja>> AbrirCaja([FromBody] JsonElement body)
    {
        // 1) Extraer datos del body (tolerante a tipos)
        decimal saldoInicial = 0m;
        int empleadoId = 0;
        DateTime? fechaEnviada = null;
        int windowMinutes = 5; // por defecto 5 minutos

        try
        {
            if (body.ValueKind == JsonValueKind.Object)
            {
                // saldoInicial
                if (body.TryGetProperty("saldoInicial", out var sprop))
                {
                    if (sprop.ValueKind == JsonValueKind.Number && sprop.TryGetDecimal(out var sd)) saldoInicial = sd;
                    else if (sprop.ValueKind == JsonValueKind.String && decimal.TryParse(sprop.GetString(), out var sd2)) saldoInicial = sd2;
                }

                // empleadoId
                if (body.TryGetProperty("empleadoId", out var eprop))
                {
                    if (eprop.ValueKind == JsonValueKind.Number && eprop.TryGetInt32(out var ei)) empleadoId = ei;
                    else if (eprop.ValueKind == JsonValueKind.String && int.TryParse(eprop.GetString(), out var ei2)) empleadoId = ei2;
                }

                // fecha (string ISO o epoch)
                if (body.TryGetProperty("fecha", out var fprop) || body.TryGetProperty("Fecha", out fprop))
                {
                    if (fprop.ValueKind == JsonValueKind.String)
                    {
                        var s = fprop.GetString();
                        if (DateTime.TryParse(s, null, System.Globalization.DateTimeStyles.AdjustToUniversal | System.Globalization.DateTimeStyles.AssumeUniversal, out var dt))
                        {
                            fechaEnviada = DateTime.SpecifyKind(dt, DateTimeKind.Utc).ToUniversalTime();
                        }
                    }
                    else if (fprop.ValueKind == JsonValueKind.Number && fprop.TryGetInt64(out var epoch))
                    {
                        // epoch en segundos o ms
                        if (epoch > 1_000_000_000_000) // ms
                            fechaEnviada = DateTimeOffset.FromUnixTimeMilliseconds(epoch).UtcDateTime;
                        else
                            fechaEnviada = DateTimeOffset.FromUnixTimeSeconds(epoch).UtcDateTime;
                    }
                }

                // windowMinutes
                if (body.TryGetProperty("windowMinutes", out var wprop) || body.TryGetProperty("ventanaMinutos", out wprop))
                {
                    if (wprop.ValueKind == JsonValueKind.Number && wprop.TryGetInt32(out var wm)) { if (wm > 0) windowMinutes = wm; }
                    else if (wprop.ValueKind == JsonValueKind.String && int.TryParse(wprop.GetString(), out var wm2)) { if (wm2 > 0) windowMinutes = wm2; }
                }
            }
        }
        catch
        {
            // En caso de fallo de parseo, seguimos con valores por defecto
            fechaEnviada = null;
        }

        // 2) Fecha a usar: fecha enviada o UtcNow
        var fecha = fechaEnviada?.ToUniversalTime() ?? DateTime.UtcNow;

        // 3) Calcular ventana: si windowMinutes >= 1440 usamos el día completo
        DateTime start, end;
        if (windowMinutes >= 1440)
        {
            start = fecha.Date;
            end = start.AddDays(1);
        }
        else
        {
            start = fecha;
            end = fecha.AddMinutes(windowMinutes);
        }

        // 4) Buscar si hay ya una caja ABIERTA en la ventana (solo ABIERTA bloquea)
        var cajaAbiertaEnVentana = await _context.Cajas
            .FirstOrDefaultAsync(c => c.Estado == EstadoCajaEnum.Abierta && c.Fecha >= start && c.Fecha < end);

        if (cajaAbiertaEnVentana != null)
        {
            return BadRequest(new { message = "Ya existe una caja abierta en la ventana de tiempo proporcionada" });
        }

        // 5) Crear la caja (cajas cerradas no bloquean)
        var caja = new Caja
        {
            Fecha = fecha,
            SaldoInicial = saldoInicial,
            SaldoFinal = 0,
            Estado = EstadoCajaEnum.Abierta,
            EmpleadoId = empleadoId
        };

        _context.Cajas.Add(caja);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetCaja), new { id = caja.Id }, caja);
    }

    // POST: api/Caja/5/cerrar
    [HttpPost("{id}/cerrar")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<ResumenCajaDto>> CerrarCaja(int id, CerrarCajaDto dto)
    {
        var caja = await _context.Cajas
            .Include(c => c.Ventas)
            .Include(c => c.Empleado)
            .FirstOrDefaultAsync(c => c.Id == id);

        if (caja == null)
        {
            return NotFound(new { message = "Caja no encontrada" });
        }

        if (caja.Estado == EstadoCajaEnum.Cerrada)
        {
            return BadRequest(new { message = "La caja ya está cerrada" });
        }

        caja.SaldoFinal = dto.SaldoFinal;
        caja.Estado = EstadoCajaEnum.Cerrada;

        await _context.SaveChangesAsync();

        // Generar resumen
        var resumen = new ResumenCajaDto
        {
            Id = caja.Id,
            Fecha = caja.Fecha,
            SaldoInicial = caja.SaldoInicial,
            SaldoFinal = caja.SaldoFinal,
            TotalVentas = caja.Ventas.Sum(v => v.Total),
            CantidadVentas = caja.Ventas.Count,
            EmpleadoNombre = caja.Empleado != null ? $"{caja.Empleado.Nombre} {caja.Empleado.Apellidos}" : null
        };

        resumen.VentasPorMetodoPago = caja.Ventas
            .GroupBy(v => v.MetodoPago.ToString())
            .ToDictionary(g => g.Key, g => g.Sum(v => v.Total));

        return resumen;
    }

    // GET: api/Caja/5/resumen
    [HttpGet("{id}/resumen")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<ResumenCajaDto>> GetResumenCaja(int id)
    {
        var caja = await _context.Cajas
            .Include(c => c.Ventas)
            .Include(c => c.Empleado)
            .FirstOrDefaultAsync(c => c.Id == id);

        if (caja == null)
        {
            return NotFound(new { message = "Caja no encontrada" });
        }

        var resumen = new ResumenCajaDto
        {
            Id = caja.Id,
            Fecha = caja.Fecha,
            SaldoInicial = caja.SaldoInicial,
            SaldoFinal = caja.SaldoFinal,
            TotalVentas = caja.Ventas.Sum(v => v.Total),
            CantidadVentas = caja.Ventas.Count,
            EmpleadoNombre = caja.Empleado != null ? $"{caja.Empleado.Nombre} {caja.Empleado.Apellidos}" : null
        };

        resumen.VentasPorMetodoPago = caja.Ventas
            .GroupBy(v => v.MetodoPago.ToString())
            .ToDictionary(g => g.Key, g => g.Sum(v => v.Total));

        return resumen;
    }

    private bool CajaExists(int id)
    {
        return _context.Cajas.Any(e => e.Id == id);
    }
}