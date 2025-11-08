using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class Cliente
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    public int PersonaId { get; set; }

    public string? ComentariosDomicilio { get; set; }

    [Column(TypeName = "decimal(6,2)")]
    public decimal? DistanciaEstimacion { get; set; }

    [Timestamp]
    public byte[] RowVersion { get; set; } = null!;

    // Relaciones
    public virtual Persona Persona { get; set; } = null!;
    public virtual ICollection<Pedido> Pedidos { get; set; } = new List<Pedido>();

    // Cliente now stores contact/direction data in Persona. Legacy compatibility aliases removed.
    public bool Activo { get; set; } = true;
}