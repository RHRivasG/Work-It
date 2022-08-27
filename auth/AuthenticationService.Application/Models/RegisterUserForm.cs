namespace AuthenticationService.Application.Models;

public readonly struct RegisterUserForm
{
    public string Username { get; init; }
    public string Password { get; init; }
    public string[] Roles { get; init; }
    public string[] Preferences { get; init; }
}