namespace AuthenticationService.Application.Models;

public readonly record struct RegisterUserForm
{
  public string Name { get; init; }
  public string Password { get; init; }
  public string[] Role { get; init; }
  public string[] Preferences { get; init; }
}
