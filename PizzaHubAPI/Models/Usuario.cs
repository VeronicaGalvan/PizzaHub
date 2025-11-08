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
    [MaxLength(255)]
    public string ContraseñaHash { get; set; } = null!;
    
    [Required]
    public UsuarioRolEnum Rol { get; set; } = UsuarioRolEnum.Cliente;
    
    [Required]
    public UsuarioEstado Estado { get; set; } = UsuarioEstado.Activo;
    
    public DateTime FechaRegistro { get; set; }
    
    [Timestamp]
    public byte[] RowVersion { get; set; } = null!;

    // Relaciones
    public virtual ICollection<TokenRevocado> TokensRevocados { get; set; } = new List<TokenRevocado>();
    
    // One-to-one relationship with Persona
    public virtual Persona? Persona { get; set; }
    
    // Navigation properties based on role
    public virtual Cliente? Cliente { get; set; }
    public virtual Repartidor? Repartidor { get; set; }
}

public enum UsuarioRolEnum
{
    Administrador,
    Empleado,
    Repartidor,
    Cliente
}

public enum UsuarioEstado
{
    Activo,
    Inactivo
}