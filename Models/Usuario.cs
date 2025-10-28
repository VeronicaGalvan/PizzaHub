using System.ComponentModel.DataAnnotations;

namespace PizzaHubAPI.Models
{
    public class Usuario
    {
        [Key]
        public int Id { get; set; }

        [Required]
        [StringLength(100)]
        public string Nombre { get; set; }

        [Required]
        [StringLength(100)]
        public string Apellidos { get; set; }

        [Required]
        [EmailAddress]
        public string Email { get; set; }

        [Required]
        public string PasswordHash { get; set; }

        [Phone]
        public string? Telefono { get; set; }

        public string? DireccionPredeterminada { get; set; }

        [Required]
        public bool Activo { get; set; } = true;

        public DateTime FechaCreacion { get; set; } = DateTime.UtcNow;
        public DateTime? UltimoAcceso { get; set; }

        // Token de refresco para autenticación
        public string? RefreshToken { get; set; }
        public DateTime? RefreshTokenExpiracion { get; set; }

        // Token FCM para notificaciones push
        public string? TokenDispositivo { get; set; }

        // Relaciones
        public virtual ICollection<UsuarioRol> UsuarioRoles { get; set; }
        public virtual ICollection<Pedido> Pedidos { get; set; }
        public virtual ICollection<MensajeChat> MensajesChat { get; set; }
        public virtual ICollection<Notificacion> Notificaciones { get; set; }
        public virtual Repartidor? Repartidor { get; set; }
    }
}