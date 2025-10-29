namespace PizzaHubAPI.Models.DTOs
{
    public class UsuarioDTO
    {
        public int Id { get; set; }
        public string Nombre { get; set; }
        public string Apellidos { get; set; }
        public string Email { get; set; }
        public string? Telefono { get; set; }
        public string? DireccionPredeterminada { get; set; }
        public bool Activo { get; set; }
        public List<string> Roles { get; set; }
    }

    public class CrearUsuarioDTO
    {
        public string Nombre { get; set; }
        public string Apellidos { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string? Telefono { get; set; }
        public string? DireccionPredeterminada { get; set; }
        public List<string> Roles { get; set; }
    }

    public class ActualizarUsuarioDTO
    {
        public string? Nombre { get; set; }
        public string? Apellidos { get; set; }
        public string? Email { get; set; }
        public string? Password { get; set; }
        public string? Telefono { get; set; }
        public string? DireccionPredeterminada { get; set; }
        public List<string>? Roles { get; set; }
    }
}