using System.Security.Claims;
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
public class ClientesController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public ClientesController(PizzaHubContext context)
    {
        _context = context;
    }

    // GET: api/Clientes/mi-perfil
    /// <summary>
    /// Obtiene el perfil del cliente autenticado
    /// </summary>
    [HttpGet("mi-perfil")]
    [Authorize(Roles = "Cliente")]
    public async Task<ActionResult<Cliente>> GetMiPerfil()
    {
        var usuarioId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var cliente = await _context.Clientes
            .Include(c => c.Usuario)
            .FirstOrDefaultAsync(c => c.UsuarioId == usuarioId);

        if (cliente == null)
            return NotFound(new { message = "Perfil de cliente no encontrado" });

        return cliente;
    }

    // PUT: api/Clientes/mi-perfil
    /// <summary>
    /// Crea o actualiza el perfil del cliente autenticado
    /// </summary>
    [HttpPut("mi-perfil")]
    [Authorize(Roles = "Cliente")]
    public async Task<IActionResult> ActualizarMiPerfil(ActualizarMiPerfilDto dto)
    {
        var usuarioId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var cliente = await _context.Clientes
            .FirstOrDefaultAsync(c => c.UsuarioId == usuarioId);

        if (cliente == null)
        {
            // Si no existe, crear el perfil
            cliente = new Cliente
            {
                Nombre = dto.Nombre,
                Apellidos = dto.Apellidos,
                Telefono = dto.Telefono,
                Colonia = dto.Colonia,
                Calle = dto.Calle,
                NumeroCasa = dto.NumeroCasa,
                Observaciones = dto.Observaciones,
                UsuarioId = usuarioId
            };

            _context.Clientes.Add(cliente);
        }
        else
        {
            // Si existe, actualizar
            cliente.Nombre = dto.Nombre;
            cliente.Apellidos = dto.Apellidos;
            cliente.Telefono = dto.Telefono;
            cliente.Colonia = dto.Colonia;
            cliente.Calle = dto.Calle;
            cliente.NumeroCasa = dto.NumeroCasa;
            cliente.Observaciones = dto.Observaciones;
        }

        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException)
        {
            throw;
        }

        return NoContent();
    }

    // GET: api/Clientes
    [HttpGet]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<IEnumerable<Cliente>>> GetClientes()
    {
        return await _context.Clientes
            .Include(c => c.Usuario)
            .ToListAsync();
    }

    // GET: api/Clientes/5
    [HttpGet("{id}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<Cliente>> GetCliente(int id)
    {
        var cliente = await _context.Clientes
            .Include(c => c.Usuario)
            .FirstOrDefaultAsync(c => c.Id == id);

        if (cliente == null)
            return NotFound(new { message = "Cliente no encontrado" });

        return cliente;
    }

    // GET: api/Clientes/usuario/5
    [HttpGet("usuario/{usuarioId}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<Cliente>> GetClientePorUsuario(int usuarioId)
    {
        var cliente = await _context.Clientes
            .Include(c => c.Usuario)
            .FirstOrDefaultAsync(c => c.UsuarioId == usuarioId);

        if (cliente == null)
            return NotFound(new { message = "Cliente no encontrado para el usuario" });

        return cliente;
    }

    // GET: api/Clientes/buscar?telefono=4771234567&nombre=Juan
    /// <summary>
    /// Busca clientes por teléfono o nombre (útil para pedidos por llamada)
    /// </summary>
    [HttpGet("buscar")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<IEnumerable<Cliente>>> BuscarCliente(
        [FromQuery] string? telefono = null,
        [FromQuery] string? nombre = null)
    {
        if (string.IsNullOrWhiteSpace(telefono) && string.IsNullOrWhiteSpace(nombre))
        {
            return BadRequest(new { message = "Debe proporcionar al menos un criterio de búsqueda (telefono o nombre)" });
        }

        var query = _context.Clientes.Include(c => c.Usuario).AsQueryable();
        
        if (!string.IsNullOrWhiteSpace(telefono))
        {
            query = query.Where(c => c.Telefono != null && c.Telefono.Contains(telefono));
        }
        
        if (!string.IsNullOrWhiteSpace(nombre))
        {
            query = query.Where(c => c.Nombre.Contains(nombre) || c.Apellidos.Contains(nombre));
        }
        
        var clientes = await query
            .OrderByDescending(c => c.Id)
            .Take(20)
            .ToListAsync();
        
        if (!clientes.Any())
        {
            return NotFound(new { message = "No se encontraron clientes con los criterios especificados" });
        }
        
        return clientes;
    }

    // POST: api/Clientes
    [HttpPost]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<Cliente>> CreateCliente(CrearClienteDto dto)
    {
        // Si se envía UsuarioId, consideramos válido sólo si es mayor a 0.
        // Tratamos 0 o valores negativos como ausencia de usuario (creación sin cuenta ligada).
        if (dto.UsuarioId.HasValue && dto.UsuarioId.Value > 0)
        {
            var usuario = await _context.Usuarios.FindAsync(dto.UsuarioId.Value);
            if (usuario == null)
                return BadRequest(new { message = "Usuario no encontrado" });
        }

        var cliente = new Cliente
        {
            Nombre = dto.Nombre,
            Apellidos = dto.Apellidos,
            Telefono = dto.Telefono,
            Colonia = dto.Colonia,
            Calle = dto.Calle,
            NumeroCasa = dto.NumeroCasa,
            Observaciones = dto.Observaciones,
            UsuarioId = (dto.UsuarioId.HasValue && dto.UsuarioId.Value > 0) ? dto.UsuarioId : null
        };

        _context.Clientes.Add(cliente);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetCliente), new { id = cliente.Id }, cliente);
    }

    // PUT: api/Clientes/5
    [HttpPut("{id}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<IActionResult> UpdateCliente(int id, CrearClienteDto dto)
    {
        var cliente = await _context.Clientes.FindAsync(id);
        if (cliente == null)
            return NotFound(new { message = "Cliente no encontrado" });

        cliente.Nombre = dto.Nombre;
        cliente.Apellidos = dto.Apellidos;
        cliente.Telefono = dto.Telefono;
        cliente.Colonia = dto.Colonia;
        cliente.Calle = dto.Calle;
        cliente.NumeroCasa = dto.NumeroCasa;
        cliente.Observaciones = dto.Observaciones;
        cliente.UsuarioId = (dto.UsuarioId.HasValue && dto.UsuarioId.Value > 0) ? dto.UsuarioId : null;

        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException)
        {
            if (!ClienteExists(id))
                return NotFound();
            throw;
        }

        return NoContent();
    }

    // DELETE: api/Clientes/5
    [HttpDelete("{id}")]
    [Authorize(Roles = "Administrador")]
    public async Task<IActionResult> DeleteCliente(int id)
    {
        var cliente = await _context.Clientes.FindAsync(id);
        if (cliente == null)
            return NotFound(new { message = "Cliente no encontrado" });

        _context.Clientes.Remove(cliente);
        await _context.SaveChangesAsync();

        return NoContent();
    }

    private bool ClienteExists(int id)
    {
        return _context.Clientes.Any(c => c.Id == id);
    }
}
