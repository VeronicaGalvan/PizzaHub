using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class TokenRevocado
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    public string Token { get; set; } = null!;
    
    public DateTime FechaRevocacion { get; set; }
    
    public int UsuarioId { get; set; }
    
    [ForeignKey("UsuarioId")]
    public virtual Usuario Usuario { get; set; } = null!;
}