namespace AuthenticationService.Models;

public record RegisterUserForm(Guid Id, string Name, string Password, string Role, string[] Preferences);