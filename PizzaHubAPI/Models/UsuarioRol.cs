// Obsoleted: role linking table removed in favor of enum-based roles on Usuario
// Kept as a stub to avoid breaking compiled migration snapshots that reference the type name.
namespace PizzaHubAPI.Models;

[System.Obsolete("UsuarioRol is kept only for backward-compatibility with migrations. Use Usuario.Rol enum instead.")]
public class UsuarioRol { }