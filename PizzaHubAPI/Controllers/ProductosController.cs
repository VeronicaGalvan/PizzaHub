using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class ProductosController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public ProductosController(PizzaHubContext context)
    {
        _context = context;
    }

    // GET: api/Productos
    [HttpGet]
    public async Task<ActionResult<IEnumerable<Producto>>> GetProductos()
    {
        return await _context.Productos.ToListAsync();
    }

    // GET: api/Productos/activos
    [HttpGet("activos")]
    public async Task<ActionResult<IEnumerable<Producto>>> GetProductosActivos()
    {
        return await _context.Productos
            .Where(p => p.Activo)
            .ToListAsync();
    }

    // GET: api/Productos/tipo/pizza
    [HttpGet("tipo/{tipo}")]
    public async Task<ActionResult<IEnumerable<Producto>>> GetProductosPorTipo(string tipo)
    {
        return await _context.Productos
            .Where(p => p.Tipo == tipo && p.Activo)
            .ToListAsync();
    }

    // GET: api/Productos/5
    [HttpGet("{id}")]
    public async Task<ActionResult<Producto>> GetProducto(int id)
    {
        var producto = await _context.Productos.FindAsync(id);

        if (producto == null)
            return NotFound(new { message = "Producto no encontrado" });

        return producto;
    }

    // POST: api/Productos
    [HttpPost]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<Producto>> CreateProducto(CrearProductoDto dto)
    {
        var producto = new Producto
        {
            Nombre = dto.Nombre,
            Descripcion = dto.Descripcion,
            Tipo = dto.Tipo,
            Precio = dto.Precio,
            Almacenable = dto.Almacenable,
            ImagenUrl = dto.ImagenUrl,
            Activo = true
        };

        _context.Productos.Add(producto);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetProducto), new { id = producto.Id }, producto);
    }

    // PUT: api/Productos/5
    [HttpPut("{id}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<IActionResult> UpdateProducto(int id, CrearProductoDto dto)
    {
        var producto = await _context.Productos.FindAsync(id);
        if (producto == null)
            return NotFound(new { message = "Producto no encontrado" });

        producto.Nombre = dto.Nombre;
        producto.Descripcion = dto.Descripcion;
        producto.Tipo = dto.Tipo;
        producto.Precio = dto.Precio;
        producto.Almacenable = dto.Almacenable;
        producto.ImagenUrl = dto.ImagenUrl;

        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException)
        {
            if (!ProductoExists(id))
                return NotFound();
            throw;
        }

        return NoContent();
    }

    // PATCH: api/Productos/5/activar
    [HttpPatch("{id}/activar")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<IActionResult> ActivarProducto(int id)
    {
        var producto = await _context.Productos.FindAsync(id);
        if (producto == null)
            return NotFound(new { message = "Producto no encontrado" });

        producto.Activo = true;
        await _context.SaveChangesAsync();

        return NoContent();
    }

    // PATCH: api/Productos/5/desactivar
    [HttpPatch("{id}/desactivar")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<IActionResult> DesactivarProducto(int id)
    {
        var producto = await _context.Productos.FindAsync(id);
        if (producto == null)
            return NotFound(new { message = "Producto no encontrado" });

        producto.Activo = false;
        await _context.SaveChangesAsync();

        return NoContent();
    }

    // DELETE: api/Productos/5
    [HttpDelete("{id}")]
    [Authorize(Roles = "Administrador")]
    public async Task<IActionResult> DeleteProducto(int id)
    {
        var producto = await _context.Productos.FindAsync(id);
        if (producto == null)
            return NotFound(new { message = "Producto no encontrado" });

        // Soft delete
        producto.Activo = false;
        await _context.SaveChangesAsync();

        return NoContent();
    }

    private bool ProductoExists(int id)
    {
        return _context.Productos.Any(p => p.Id == id);
    }
}
