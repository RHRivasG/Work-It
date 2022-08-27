namespace AuthenticationService.Application.Models;

public readonly struct UpdateUserForm
{
    public Guid Id { get; init; }
    public string Username { get; init; }
    public string Password { get; init; }
    public string[] Preferences { get; init; }
}