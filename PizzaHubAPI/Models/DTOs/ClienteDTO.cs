namespace PizzaHubAPI.Models.DTOs;

public class RegistroClienteDTO
{
    public string Email { get; set; } = null!;
    public string Password { get; set; } = null!;
    public string NumeroCelular { get; set; } = null!;
    public string Nombre { get; set; } = null!;
    public string Apellido { get; set; } = null!;
    public string Colonia { get; set; } = null!;
    public string Calle { get; set; } = null!;
    public string Numero { get; set; } = null!;
    public decimal DistanciaAproximada { get; set; }
}

public class ClienteDTO
{
    public int Id { get; set; }
    public string NumeroCelular { get; set; } = null!;
    public string Nombre { get; set; } = null!;
    public string Apellido { get; set; } = null!;
    public string Colonia { get; set; } = null!;
    public string Calle { get; set; } = null!;
    public string Numero { get; set; } = null!;
    public decimal DistanciaAproximada { get; set; }
    public DateTime FechaRegistro { get; set; }
}