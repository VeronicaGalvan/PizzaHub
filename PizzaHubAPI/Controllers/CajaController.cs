using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;

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
    [HttpPost("abrir")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<Caja>> AbrirCaja(AbrirCajaDto dto)
    {
        // Verificar que no haya una caja abierta
        var cajaAbierta = await _context.Cajas
            .FirstOrDefaultAsync(c => c.Estado == EstadoCajaEnum.Abierta);

        if (cajaAbierta != null)
        {
            return BadRequest(new { message = "Ya existe una caja abierta" });
        }

        // Verificar que no exista una caja para la fecha actual
        var fechaHoy = DateTime.UtcNow.Date;
        var cajaHoy = await _context.Cajas
            .FirstOrDefaultAsync(c => c.Fecha.Date == fechaHoy);

        if (cajaHoy != null)
        {
            return BadRequest(new { message = "Ya existe una caja para la fecha actual" });
        }

        var caja = new Caja
        {
            Fecha = fechaHoy,
            SaldoInicial = dto.SaldoInicial,
            SaldoFinal = 0,
            Estado = EstadoCajaEnum.Abierta,
            EmpleadoId = dto.EmpleadoId
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

        // Calcular ventas por método de pago
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

        // Calcular ventas por método de pago
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
