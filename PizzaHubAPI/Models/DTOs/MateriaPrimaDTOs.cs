using System.ComponentModel.DataAnnotations;

namespace PizzaHubAPI.Models.DTOs;

public class CrearMateriaPrimaDTO
{
    [Required]
    public decimal CantidadActual { get; set; }

    [Required]
    [MaxLength(100)]
    public string Nombre { get; set; } = null!;

    public int? ProveedorId { get; set; }

    [Required]
    public decimal PuntoReorden { get; set; }

    [Required]
    public int UnidadMedida { get; set; }
}
