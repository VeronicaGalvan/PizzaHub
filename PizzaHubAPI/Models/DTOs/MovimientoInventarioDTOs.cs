using System.ComponentModel.DataAnnotations;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Models.DTOs;

public class CrearMovimientoInventarioDTO
{
    [Required]
    public int ProductoId { get; set; }

    [Required]
    public TipoMovimiento Tipo { get; set; }

    [Required]
    public int Cantidad { get; set; }

    [Required]
    [MaxLength(255)]
    public string Motivo { get; set; } = null!;

    [MaxLength(500)]
    public string? Observaciones { get; set; }
}
