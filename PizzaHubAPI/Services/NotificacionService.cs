using FirebaseAdmin;
using FirebaseAdmin.Messaging;
using Google.Apis.Auth.OAuth2;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Services;

public class NotificacionService
{
    private readonly PizzaHubContext _context;
    private readonly IConfiguration _configuration;
    private readonly ILogger<NotificacionService> _logger;
    private static bool _firebaseInitialized = false;
    private static readonly object _lock = new object();

    public NotificacionService(
        PizzaHubContext context,
        IConfiguration configuration,
        ILogger<NotificacionService> logger)
    {
        _context = context;
        _configuration = configuration;
        _logger = logger;
        InitializeFirebase();
    }

    private void InitializeFirebase()
    {
        if (_firebaseInitialized) return;

        lock (_lock)
        {
            if (_firebaseInitialized) return;

            try
            {
                var firebaseConfigPath = _configuration["Firebase:CredentialsPath"];

                if (string.IsNullOrEmpty(firebaseConfigPath) || !File.Exists(firebaseConfigPath))
                {
                    _logger.LogWarning("Firebase credentials file not found. Push notifications will not work.");
                    return;
                }

                _firebaseInitialized = true;
                _logger.LogInformation("Firebase initialized successfully");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error initializing Firebase");
            }
        }
    }

    /// <summary>
    /// Envía una notificación push a un cliente específico
    /// </summary>
    public async Task<bool> EnviarNotificacionPushAsync(int clienteId, string titulo, string mensaje, string tipo, int? pedidoId = null)
    {
        try
        {
            var cliente = await _context.Clientes.FindAsync(clienteId);

            if (cliente == null)
            {
                _logger.LogWarning($"Cliente {clienteId} no encontrado");
                return false;
            }

            // Guardar notificación en la base de datos
            var notificacion = new Notificacion
            {
                ClienteId = clienteId,
                Titulo = titulo,
                Mensaje = mensaje,
                Tipo = tipo,
                PedidoId = pedidoId,
                Enviada = false,
                Leida = false,
                FechaCreacion = DateTime.UtcNow
            };

            _context.Notificaciones.Add(notificacion);
            await _context.SaveChangesAsync();

            // Intentar enviar push si hay token FCM
            if (!string.IsNullOrEmpty(cliente.FcmToken) && _firebaseInitialized)
            {
                var pushEnviado = await EnviarPushFCMAsync(cliente.FcmToken, titulo, mensaje, pedidoId);

                if (pushEnviado)
                {
                    notificacion.Enviada = true;
                    await _context.SaveChangesAsync();
                }
            }
            else
            {
                _logger.LogInformation($"Cliente {clienteId} no tiene FCM token o Firebase no está inicializado");
            }

            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"Error enviando notificación a cliente {clienteId}");
            return false;
        }
    }

    /// <summary>
    /// Envía una notificación push mediante FCM
    /// </summary>
    private async Task<bool> EnviarPushFCMAsync(string fcmToken, string titulo, string mensaje, int? pedidoId)
    {
        try
        {
            var message = new Message()
            {
                Token = fcmToken,
                Notification = new Notification()
                {
                    Title = titulo,
                    Body = mensaje
                },
                Data = new Dictionary<string, string>()
                {
                    { "titulo", titulo },
                    { "mensaje", mensaje },
                    { "pedido_id", pedidoId?.ToString() ?? "" },
                    { "timestamp", DateTime.UtcNow.ToString("o") }
                },
                Android = new AndroidConfig()
                {
                    Priority = Priority.High,
                    Notification = new AndroidNotification()
                    {
                        Sound = "default",
                        ChannelId = "pedidos_channel"
                    }
                },
                Apns = new ApnsConfig()
                {
                    Aps = new Aps()
                    {
                        Sound = "default"
                    }
                }
            };

            var response = await FirebaseMessaging.DefaultInstance.SendAsync(message);
            _logger.LogInformation($"Push notification sent successfully: {response}");
            return true;
        }
        catch (FirebaseMessagingException ex)
        {
            _logger.LogError(ex, $"Error sending FCM push notification. Error code: {ex.MessagingErrorCode}");

            // Si el token es inválido, lo eliminamos
            if (ex.MessagingErrorCode == MessagingErrorCode.Unregistered ||
                ex.MessagingErrorCode == MessagingErrorCode.InvalidArgument)
            {
                await InvalidarTokenFCMAsync(fcmToken);
            }

            return false;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error sending FCM push notification");
            return false;
        }
    }

    /// <summary>
    /// Registra o actualiza el token FCM de un cliente
    /// </summary>
    public async Task<bool> RegistrarTokenFCMAsync(int clienteId, string fcmToken)
    {
        try
        {
            var cliente = await _context.Clientes.FindAsync(clienteId);

            if (cliente == null)
            {
                _logger.LogWarning($"Cliente {clienteId} no encontrado");
                return false;
            }

            cliente.FcmToken = fcmToken;
            await _context.SaveChangesAsync();

            _logger.LogInformation($"Token FCM registrado para cliente {clienteId}");
            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"Error registrando token FCM para cliente {clienteId}");
            return false;
        }
    }

    /// <summary>
    /// Elimina el token FCM de un cliente (cuando cierra sesión o desinstala la app)
    /// </summary>
    public async Task<bool> EliminarTokenFCMAsync(int clienteId)
    {
        try
        {
            var cliente = await _context.Clientes.FindAsync(clienteId);

            if (cliente == null)
            {
                return false;
            }

            cliente.FcmToken = null;
            await _context.SaveChangesAsync();

            _logger.LogInformation($"Token FCM eliminado para cliente {clienteId}");
            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"Error eliminando token FCM para cliente {clienteId}");
            return false;
        }
    }

    /// <summary>
    /// Invalida un token FCM que ya no es válido
    /// </summary>
    private async Task InvalidarTokenFCMAsync(string fcmToken)
    {
        try
        {
            var clientes = await _context.Clientes
                .Where(c => c.FcmToken == fcmToken)
                .ToListAsync();

            foreach (var cliente in clientes)
            {
                cliente.FcmToken = null;
            }

            await _context.SaveChangesAsync();
            _logger.LogInformation($"Token FCM inválido removido de {clientes.Count} cliente(s)");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error invalidando token FCM");
        }
    }

    /// <summary>
    /// Obtiene las notificaciones de un cliente
    /// </summary>
    public async Task<List<Notificacion>> ObtenerNotificacionesClienteAsync(int clienteId, bool soloNoLeidas = false)
    {
        try
        {
            var query = _context.Notificaciones
                .Where(n => n.ClienteId == clienteId);

            if (soloNoLeidas)
            {
                query = query.Where(n => !n.Leida);
            }

            return await query
                .OrderByDescending(n => n.FechaCreacion)
                .ToListAsync();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"Error obteniendo notificaciones del cliente {clienteId}");
            return new List<Notificacion>();
        }
    }

    /// <summary>
    /// Marca una notificación como leída
    /// </summary>
    public async Task<bool> MarcarComoLeidaAsync(int notificacionId, int clienteId)
    {
        try
        {
            var notificacion = await _context.Notificaciones
                .FirstOrDefaultAsync(n => n.Id == notificacionId && n.ClienteId == clienteId);

            if (notificacion == null)
            {
                return false;
            }

            notificacion.Leida = true;
            notificacion.FechaLectura = DateTime.UtcNow;
            await _context.SaveChangesAsync();

            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"Error marcando notificación {notificacionId} como leída");
            return false;
        }
    }

    /// <summary>
    /// Marca todas las notificaciones de un cliente como leídas
    /// </summary>
    public async Task<bool> MarcarTodasComoLeidasAsync(int clienteId)
    {
        try
        {
            var notificaciones = await _context.Notificaciones
                .Where(n => n.ClienteId == clienteId && !n.Leida)
                .ToListAsync();

            foreach (var notificacion in notificaciones)
            {
                notificacion.Leida = true;
                notificacion.FechaLectura = DateTime.UtcNow;
            }

            await _context.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"Error marcando todas las notificaciones como leídas para cliente {clienteId}");
            return false;
        }
    }

    /// <summary>
    /// Envía notificación cuando cambia el estado de un pedido
    /// </summary>
    public async Task NotificarCambioEstadoPedidoAsync(int pedidoId, EstadoPedidoEnum nuevoEstado)
    {
        try
        {
            var pedido = await _context.Pedidos
                .Include(p => p.Cliente)
                .FirstOrDefaultAsync(p => p.Id == pedidoId);

            if (pedido == null || pedido.ClienteId == null)
            {
                return;
            }

            string titulo = "Actualización de tu pedido";
            string mensaje = nuevoEstado switch
            {
                EstadoPedidoEnum.Pendiente => "Tu pedido ha sido recibido y está pendiente de confirmación.",
                EstadoPedidoEnum.EnPreparacion => "Tu pedido está siendo preparado.",
                EstadoPedidoEnum.EnCamino => "Tu pedido está en camino.",
                EstadoPedidoEnum.Entregado => "¡Tu pedido ha sido entregado! ¡Buen provecho!",
                EstadoPedidoEnum.Cancelado => "Tu pedido ha sido cancelado.",
                _ => "El estado de tu pedido ha cambiado."
            };

            await EnviarNotificacionPushAsync(
                pedido.ClienteId.Value,
                titulo,
                mensaje,
                "pedido",
                pedidoId
            );
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"Error notificando cambio de estado del pedido {pedidoId}");
        }
    }
}
