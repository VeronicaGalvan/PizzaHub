using System.ComponentModel.DataAnnotations;

namespace PizzaHubAPI.Models
{
    public class Notificacion
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public int UsuarioId { get; set; }

        [Required]
        public string Titulo { get; set; }

        [Required]
        public string Mensaje { get; set; }

        public string? Datos { get; set; }

        [Required]
        public bool Leida { get; set; } = false;

        [Required]
        public DateTime FechaCreacion { get; set; } = DateTime.UtcNow;

        public DateTime? FechaLectura { get; set; }

        // Para FCM (Firebase Cloud Messaging)
        public string? TokenDispositivo { get; set; }

        // Relaciones
        public virtual Usuario Usuario { get; set; }
    }
}