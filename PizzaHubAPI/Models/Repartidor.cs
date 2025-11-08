using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class Repartidor
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    public int PersonaId { get; set; }

    [MaxLength(50)]
    public string? Sobrenombre { get; set; }

    public string? VehiculoAsignado { get; set; }

    public string? HorarioTrabajo { get; set; }

    public bool Disponible { get; set; } = false;

    public bool Activo { get; set; } = true;

    [Timestamp]
    public byte[] RowVersion { get; set; } = null!;

    // Relaciones
    public virtual Persona Persona { get; set; } = null!;
    public virtual ICollection<Pedido> Pedidos { get; set; } = new List<Pedido>();

    // Legacy compatibility aliases removed. Use Persona for contact and address fields.
}