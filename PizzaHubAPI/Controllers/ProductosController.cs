using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class ProductosController : ControllerBase
{
    private readonly PizzaHubContext _context;
    private readonly IWebHostEnvironment _environment;

    public ProductosController(PizzaHubContext context, IWebHostEnvironment environment)
    {
        _context = context;
        _environment = environment;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<Producto>>> GetProductos()
    {
        return await _context.Productos.Where(p => p.Activo).ToListAsync();
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<Producto>> GetProducto(int id)
    {
        var producto = await _context.Productos.FindAsync(id);

        if (producto == null || !producto.Activo)
        {
            return NotFound();
        }

        return producto;
    }

    [Authorize(Roles = "Admin")]
    [HttpPost]
    public async Task<ActionResult<Producto>> CreateProducto([FromForm] Producto producto, IFormFile? imagen)
    {
        if (imagen != null)
        {
            var fileName = Guid.NewGuid().ToString() + Path.GetExtension(imagen.FileName);
            var filePath = Path.Combine(_environment.WebRootPath, "productos", fileName);

            Directory.CreateDirectory(Path.Combine(_environment.WebRootPath, "productos"));
            using (var stream = new FileStream(filePath, FileMode.Create))
            {
                await imagen.CopyToAsync(stream);
            }

            producto.RutaImagen = "/productos/" + fileName;
        }

        producto.CreadoEn = DateTime.UtcNow;
        _context.Productos.Add(producto);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetProducto), new { id = producto.Id }, producto);
    }

    [Authorize(Roles = "Admin")]
    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateProducto(int id, [FromForm] Producto producto, IFormFile? imagen)
    {
        if (id != producto.Id)
        {
            return BadRequest();
        }

        var existingProducto = await _context.Productos.FindAsync(id);
        if (existingProducto == null || !existingProducto.Activo)
        {
            return NotFound();
        }

        if (imagen != null)
        {
            // Eliminar imagen anterior si existe
            if (!string.IsNullOrEmpty(existingProducto.RutaImagen))
            {
                var oldFilePath = Path.Combine(_environment.WebRootPath, existingProducto.RutaImagen.TrimStart('/'));
                if (System.IO.File.Exists(oldFilePath))
                {
                    System.IO.File.Delete(oldFilePath);
                }
            }

            // Guardar nueva imagen
            var fileName = Guid.NewGuid().ToString() + Path.GetExtension(imagen.FileName);
            var filePath = Path.Combine(_environment.WebRootPath, "productos", fileName);

            Directory.CreateDirectory(Path.Combine(_environment.WebRootPath, "productos"));
            using (var stream = new FileStream(filePath, FileMode.Create))
            {
                await imagen.CopyToAsync(stream);
            }

            existingProducto.RutaImagen = "/productos/" + fileName;
        }

        existingProducto.Nombre = producto.Nombre;
        existingProducto.Descripcion = producto.Descripcion;
        existingProducto.Precio = producto.Precio;
        existingProducto.StockMinimo = producto.StockMinimo;
        existingProducto.ActualizadoEn = DateTime.UtcNow;

        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException)
        {
            if (!ProductoExists(id))
            {
                return NotFound();
            }
            else
            {
                throw;
            }
        }

        return NoContent();
    }

    [Authorize(Roles = "Admin")]
    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteProducto(int id)
    {
        var producto = await _context.Productos.FindAsync(id);
        if (producto == null || !producto.Activo)
        {
            return NotFound();
        }

        producto.Activo = false;
        producto.ActualizadoEn = DateTime.UtcNow;

        await _context.SaveChangesAsync();

        return NoContent();
    }

    private bool ProductoExists(int id)
    {
        return _context.Productos.Any(e => e.Id == id);
    }
}