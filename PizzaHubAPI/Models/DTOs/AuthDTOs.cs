namespace PizzaHubAPI.Models.DTOs;

public class LoginRequestDTO
{
    public string Email { get; set; } = null!;
    public string Password { get; set; } = null!;
}

public class LoginResponseDTO
{
    public string AccessToken { get; set; } = null!;
    public string RefreshToken { get; set; } = null!;
    public string TokenType { get; set; } = "Bearer";
    public int ExpiresIn { get; set; }
    public string[] Roles { get; set; } = null!;
    public UserInfoDTO Usuario { get; set; } = null!;
}

public class UserInfoDTO
{
    public int Id { get; set; }
    public string Email { get; set; } = null!;
    public string NombreCompleto { get; set; } = null!;
    public string? TelefonoContacto { get; set; }
}