using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class Usuario
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(100)]
    public string Email { get; set; } = null!;
    
    [Required]
    [MaxLength(100)]
    public string NombreCompleto { get; set; } = null!;
    
    [Required]
    public string PasswordHash { get; set; } = null!;
    
    public bool Activo { get; set; } = true;
    
    [MaxLength(50)]
    public string? TelefonoContacto { get; set; }
    
    [Timestamp]
    public byte[] RowVersion { get; set; } = null!;
    
    public DateTime CreadoEn { get; set; }
    
    public DateTime? ActualizadoEn { get; set; }
    
    // Relaciones
    public virtual ICollection<UsuarioRol> UsuariosRoles { get; set; } = new List<UsuarioRol>();
    public virtual ICollection<TokenRevocado> TokensRevocados { get; set; } = new List<TokenRevocado>();
}