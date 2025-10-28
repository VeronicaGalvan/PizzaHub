using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models
{
    public class Producto
    {
        [Key]
        public int Id { get; set; }
        
        [Required]
        [StringLength(100)]
        public string Nombre { get; set; }
        
        [Required]
        public string Descripcion { get; set; }
        
        [Required]
        [Column(TypeName = "decimal(18,2)")]
        public decimal Precio { get; set; }
        
        public string? ImagenUrl { get; set; }
        
        [Required]
        public int Stock { get; set; }
        
        public bool Activo { get; set; } = true;
        
        // Relaciones
        public virtual ICollection<DetallePedido> DetallesPedidos { get; set; }
    }
}