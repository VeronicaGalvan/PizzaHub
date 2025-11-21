using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;
using PizzaHubAPI.Services;
using System.Security.Claims;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
[Authorize]
public class NotificacionesController : ControllerBase
{
    private readonly NotificacionService _notificacionService;
    private readonly PizzaHubContext _context;
    private readonly ILogger<NotificacionesController> _logger;

    public NotificacionesController(
        NotificacionService notificacionService,
        PizzaHubContext context,
        ILogger<NotificacionesController> logger)
    {
        _notificacionService = notificacionService;
        _context = context;
        _logger = logger;
    }

    /// <summary>
    /// Obtiene el ID del cliente desde el token JWT
    /// </summary>
    private async Task<int?> ObtenerClienteIdDelTokenAsync()
    {
        // Primero intentar obtener directamente el ClienteId del claim
        var clienteIdClaim = User.FindFirst("ClienteId")?.Value;
        if (int.TryParse(clienteIdClaim, out int clienteId))
        {
            return clienteId;
        }

        // Fallback: obtener el cliente a partir del UsuarioId (NameIdentifier)
        var usuarioIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (int.TryParse(usuarioIdClaim, out int usuarioId))
        {
            var cliente = await _context.Clientes
                .FirstOrDefaultAsync(c => c.UsuarioId == usuarioId);

            return cliente?.Id;
        }

        return null;
    }

    /// <summary>
    /// Registra o actualiza el token FCM del cliente
    /// </summary>
    /// <param name="dto">Objeto con el token FCM</param>
    [HttpPost("registrar-token")]
    public async Task<ActionResult> RegistrarTokenFCM([FromBody] RegistrarTokenFCMDto dto)
    {
        try
        {
            var clienteId = await ObtenerClienteIdDelTokenAsync();

            if (clienteId == null)
            {
                return BadRequest(new { message = "No se pudo identificar al cliente" });
            }

            var resultado = await _notificacionService.RegistrarTokenFCMAsync(clienteId.Value, dto.FcmToken);

            if (!resultado)
            {
                return BadRequest(new { message = "No se pudo registrar el token FCM" });
            }

            return Ok(new { message = "Token FCM registrado correctamente" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error registrando token FCM");
            return StatusCode(500, new { message = "Error al registrar el token FCM" });
        }
    }

    /// <summary>
    /// Elimina el token FCM del cliente (logout o desinstalación)
    /// </summary>
    [HttpDelete("eliminar-token")]
    public async Task<ActionResult> EliminarTokenFCM()
    {
        try
        {
            var clienteId = await ObtenerClienteIdDelTokenAsync();

            if (clienteId == null)
            {
                return BadRequest(new { message = "No se pudo identificar al cliente" });
            }

            var resultado = await _notificacionService.EliminarTokenFCMAsync(clienteId.Value);

            if (!resultado)
            {
                return BadRequest(new { message = "No se pudo eliminar el token FCM" });
            }

            return Ok(new { message = "Token FCM eliminado correctamente" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error eliminando token FCM");
            return StatusCode(500, new { message = "Error al eliminar el token FCM" });
        }
    }

    /// <summary>
    /// Obtiene todas las notificaciones del cliente autenticado
    /// </summary>
    /// <param name="soloNoLeidas">Si es true, solo retorna las no leídas</param>
    [HttpGet]
    public async Task<ActionResult<List<NotificacionDto>>> ObtenerNotificaciones([FromQuery] bool soloNoLeidas = false)
    {
        try
        {
            var clienteId = await ObtenerClienteIdDelTokenAsync();

            if (clienteId == null)
            {
                return BadRequest(new { message = "No se pudo identificar al cliente" });
            }

            var notificaciones = await _notificacionService.ObtenerNotificacionesClienteAsync(
                clienteId.Value,
                soloNoLeidas
            );

            var notificacionesDto = notificaciones.Select(n => new NotificacionDto
            {
                Id = n.Id,
                Titulo = n.Titulo,
                Mensaje = n.Mensaje,
                Tipo = n.Tipo,
                PedidoId = n.PedidoId,
                Leida = n.Leida,
                Enviada = n.Enviada,
                FechaCreacion = n.FechaCreacion,
                FechaLectura = n.FechaLectura
            }).ToList();

            return Ok(notificacionesDto);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error obteniendo notificaciones");
            return StatusCode(500, new { message = "Error al obtener las notificaciones" });
        }
    }

    /// <summary>
    /// Obtiene el conteo de notificaciones no leídas
    /// </summary>
    [HttpGet("no-leidas/conteo")]
    public async Task<ActionResult<int>> ObtenerConteoNoLeidas()
    {
        try
        {
            var clienteId = await ObtenerClienteIdDelTokenAsync();

            if (clienteId == null)
            {
                return BadRequest(new { message = "No se pudo identificar al cliente" });
            }

            var notificaciones = await _notificacionService.ObtenerNotificacionesClienteAsync(
                clienteId.Value,
                true
            );

            return Ok(new { conteo = notificaciones.Count });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error obteniendo conteo de notificaciones");
            return StatusCode(500, new { message = "Error al obtener el conteo" });
        }
    }

    /// <summary>
    /// Marca una notificación específica como leída
    /// </summary>
    /// <param name="id">ID de la notificación</param>
    [HttpPut("{id}/marcar-leida")]
    public async Task<ActionResult> MarcarComoLeida(int id)
    {
        try
        {
            var clienteId = await ObtenerClienteIdDelTokenAsync();

            if (clienteId == null)
            {
                return BadRequest(new { message = "No se pudo identificar al cliente" });
            }

            var resultado = await _notificacionService.MarcarComoLeidaAsync(id, clienteId.Value);

            if (!resultado)
            {
                return NotFound(new { message = "Notificación no encontrada" });
            }

            return Ok(new { message = "Notificación marcada como leída" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"Error marcando notificación {id} como leída");
            return StatusCode(500, new { message = "Error al marcar la notificación como leída" });
        }
    }

    /// <summary>
    /// Marca todas las notificaciones del cliente como leídas
    /// </summary>
    [HttpPut("marcar-todas-leidas")]
    public async Task<ActionResult> MarcarTodasComoLeidas()
    {
        try
        {
            var clienteId = await ObtenerClienteIdDelTokenAsync();

            if (clienteId == null)
            {
                return BadRequest(new { message = "No se pudo identificar al cliente" });
            }

            var resultado = await _notificacionService.MarcarTodasComoLeidasAsync(clienteId.Value);

            if (!resultado)
            {
                return BadRequest(new { message = "No se pudieron marcar las notificaciones como leídas" });
            }

            return Ok(new { message = "Todas las notificaciones han sido marcadas como leídas" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error marcando todas las notificaciones como leídas");
            return StatusCode(500, new { message = "Error al marcar las notificaciones como leídas" });
        }
    }

    /// <summary>
    /// Envía una notificación de prueba al cliente (solo para testing)
    /// </summary>
    [HttpPost("prueba")]
    public async Task<ActionResult> EnviarNotificacionPrueba()
    {
        try
        {
            var clienteId = await ObtenerClienteIdDelTokenAsync();

            if (clienteId == null)
            {
                return BadRequest(new { message = "No se pudo identificar al cliente" });
            }

            var resultado = await _notificacionService.EnviarNotificacionPushAsync(
                clienteId.Value,
                "Notificación de prueba",
                "Esta es una notificación de prueba desde PizzaHub",
                "sistema"
            );

            if (!resultado)
            {
                return BadRequest(new { message = "No se pudo enviar la notificación de prueba" });
            }

            return Ok(new { message = "Notificación de prueba enviada correctamente" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error enviando notificación de prueba");
            return StatusCode(500, new { message = "Error al enviar la notificación de prueba" });
        }
    }
}
