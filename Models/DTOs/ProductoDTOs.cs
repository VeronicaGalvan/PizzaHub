namespace PizzaHubAPI.Models.DTOs
{
    public class ProductoDTO
    {
        public int Id { get; set; }
        public string Nombre { get; set; }
        public string Descripcion { get; set; }
        public decimal Precio { get; set; }
        public string? ImagenUrl { get; set; }
        public int Stock { get; set; }
        public bool Activo { get; set; }
    }

    public class CrearProductoDTO
    {
        public string Nombre { get; set; }
        public string Descripcion { get; set; }
        public decimal Precio { get; set; }
        public IFormFile? Imagen { get; set; }
        public int Stock { get; set; }
    }

    public class ActualizarProductoDTO
    {
        public string? Nombre { get; set; }
        public string? Descripcion { get; set; }
        public decimal? Precio { get; set; }
        public IFormFile? Imagen { get; set; }
        public int? Stock { get; set; }
    }
}