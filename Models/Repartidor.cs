using System.ComponentModel.DataAnnotations;

namespace PizzaHubAPI.Models
{
    public enum EstadoRepartidor
    {
        DISPONIBLE,
        EN_ENTREGA,
        DESCONECTADO
    }

    public class Repartidor
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public int UsuarioId { get; set; }

        [Required]
        public EstadoRepartidor Estado { get; set; } = EstadoRepartidor.DESCONECTADO;

        public DateTime? UltimaConexion { get; set; }

        [Required]
        public bool Activo { get; set; } = true;

        // Estadísticas
        public int PedidosEntregados { get; set; } = 0;
        public double CalificacionPromedio { get; set; } = 0.0;

        // Relaciones
        public virtual Usuario Usuario { get; set; }
        public virtual ICollection<Pedido> Pedidos { get; set; }
    }
}