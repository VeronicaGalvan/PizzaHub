using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class Repartidor
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(15)]
    public string NumeroCelular { get; set; } = null!;
    
    [Required]
    [MaxLength(50)]
    public string Sobrenombre { get; set; } = null!;
    
    [Required]
    [MaxLength(50)]
    public string Nombre { get; set; } = null!;
    
    [Required]
    [MaxLength(50)]
    public string Apellido { get; set; } = null!;
    
    [Required]
    [MaxLength(100)]
    public string Colonia { get; set; } = null!;
    
    [Required]
    [MaxLength(100)]
    public string Calle { get; set; } = null!;
    
    [Required]
    [MaxLength(10)]
    public string Numero { get; set; } = null!;
    
    public bool Disponible { get; set; } = true;
    
    public bool Activo { get; set; } = true;
    
    [Required]
    public int UsuarioId { get; set; }
    
    [Timestamp]
    public byte[] RowVersion { get; set; } = null!;
    
    // Relaciones
    public virtual Usuario Usuario { get; set; } = null!;
    public virtual ICollection<Pedido> Pedidos { get; set; } = new List<Pedido>();
}