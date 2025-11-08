using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class CalificacionPedido
{
    [Key]
    public int Id { get; set; }

    [Required]
    public int ClienteId { get; set; }

    public string? Comentario { get; set; }

    public DateTime FechaRegistro { get; set; }

    [Required]
    public int PedidoId { get; set; }

    [Required]
    public int Puntuacion { get; set; }

    // Relaciones
    public virtual Cliente? Cliente { get; set; }
    public virtual Pedido? Pedido { get; set; }
}
