namespace AuthenticationService.Models;

public record UpdateUserForm(Guid Id, string Name, string Password, string Role, string[] Preferences);