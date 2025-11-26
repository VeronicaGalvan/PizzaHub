using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

[Table("calificaciones")]
public class Calificacion
{
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Required]
    [Column("pedido_id")]
    public int PedidoId { get; set; }

    [Required]
    [Column("estrellas")]
    [Range(1, 5, ErrorMessage = "Las estrellas deben estar entre 1 y 5")]
    public int Estrellas { get; set; }

    [Column("comentario")]
    public string? Comentario { get; set; }

    [Column("fecha")]
    public DateTime Fecha { get; set; } = DateTime.UtcNow;

    // Relaciones
    [ForeignKey("PedidoId")]
    public virtual Pedido Pedido { get; set; } = null!;
}
