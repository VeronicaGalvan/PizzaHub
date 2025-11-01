using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public enum TipoMovimiento
{
    Entrada,
    Salida,
    Ajuste
}

public class MovimientoInventario
{
    [Key]
    public int Id { get; set; }
    
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
    
    public int? UsuarioId { get; set; }
    
    [Timestamp]
    public byte[] RowVersion { get; set; } = null!;
    
    public DateTime CreadoEn { get; set; }
    
    // Relaciones
    public virtual Producto Producto { get; set; } = null!;
    public virtual Usuario? Usuario { get; set; }
}