using System.ComponentModel.DataAnnotations;

namespace PizzaHubAPI.Models;

public class HistorialEstadoPedido
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    public int PedidoId { get; set; }
    
    [Required]
    public EstadoPedido Estado { get; set; }
    
    public int? UsuarioId { get; set; }
    
    [MaxLength(500)]
    public string? Observaciones { get; set; }
    
    public DateTime CreadoEn { get; set; }
    
    // Relaciones
    public virtual Pedido Pedido { get; set; } = null!;
    public virtual Usuario? Usuario { get; set; }
}