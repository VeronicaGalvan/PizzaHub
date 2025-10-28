namespace PizzaHubAPI.Models.DTOs
{
    public class RepartidorDTO
    {
        public int Id { get; set; }
        public int UsuarioId { get; set; }
        public string NombreCompleto { get; set; }
        public EstadoRepartidor Estado { get; set; }
        public DateTime? UltimaConexion { get; set; }
        public bool Activo { get; set; }
        public int PedidosEntregados { get; set; }
        public double CalificacionPromedio { get; set; }
    }

    public class CrearRepartidorDTO
    {
        public int UsuarioId { get; set; }
    }

    public class ActualizarEstadoRepartidorDTO
    {
        public EstadoRepartidor Estado { get; set; }
    }
}