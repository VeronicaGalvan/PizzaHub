// Obsoleted: Rol entity removed from active app model in favor of Usuario.Rol enum.
// Kept as a minimal stub so existing migration snapshots that reference the type name continue to compile.
namespace PizzaHubAPI.Models;

[System.Obsolete("Rol is kept only for backward-compatibility with migrations. Use Usuario.Rol enum instead.")]
public class Rol { }