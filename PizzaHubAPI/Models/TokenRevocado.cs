using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Security.Cryptography;
using System.Text;

namespace PizzaHubAPI.Models;

public class TokenRevocado
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(64)] // SHA256 hash en hexadecimal (64 caracteres)
    public string TokenHash { get; set; } = null!;
    
    public DateTime FechaRevocacion { get; set; }
    
    public int UsuarioId { get; set; }
    
    [ForeignKey("UsuarioId")]
    public virtual Usuario Usuario { get; set; } = null!;
    
    /// <summary>
    /// Calcula el hash SHA256 de un token para almacenamiento indexado
    /// </summary>
    public static string ComputeHash(string token)
    {
        using var sha256 = SHA256.Create();
        var hashBytes = sha256.ComputeHash(Encoding.UTF8.GetBytes(token));
        return Convert.ToHexString(hashBytes);
    }
}