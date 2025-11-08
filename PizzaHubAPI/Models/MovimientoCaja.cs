using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class MovimientoCaja
{
    [Key]
    public int Id { get; set; }

    public string? Concepto { get; set; }

    public DateTime FechaRegistro { get; set; }

    [Column(TypeName = "decimal(10,2)")]
    public decimal Monto { get; set; }

    [Required]
    public int SesionId { get; set; }

    public int TipoMovimiento { get; set; }

    public int UsuarioId { get; set; }

    // Relaciones
    public virtual SesionCaja? Sesion { get; set; }
}
