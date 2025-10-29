using System.ComponentModel.DataAnnotations;

namespace PizzaHubAPI.Models
{
    public class MensajeChat
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public int UsuarioId { get; set; }

        [Required]
        public string Mensaje { get; set; }

        [Required]
        public bool EsUsuario { get; set; }

        [Required]
        public DateTime FechaEnvio { get; set; } = DateTime.UtcNow;

        // Relaciones
        public virtual Usuario Usuario { get; set; }
    }
}