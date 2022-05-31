namespace AuthenticationService.Models;

public record AuthenticateUserForm(string Name, string Password, string Role);