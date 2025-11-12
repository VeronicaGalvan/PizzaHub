using System.ComponentModel.DataAnnotations;

namespace PizzaHubAPI.Models.DTOs;

public class CrearProductoDto
{
    [Required]
    [MaxLength(100)]
    public string Nombre { get; set; } = null!;
    
    public string? Descripcion { get; set; }
    
    [MaxLength(50)]
    public string? Tipo { get; set; }
    
    [Required]
    [Range(0.01, 99999.99)]
    public decimal Precio { get; set; }
    
    public bool Almacenable { get; set; } = false;
    
    [MaxLength(255)]
    public string? ImagenUrl { get; set; }
}
