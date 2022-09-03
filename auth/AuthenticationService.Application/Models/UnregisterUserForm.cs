namespace AuthenticationService.Application.Models;

public readonly record struct UnregisterUserForm
{
  public Guid Id { get; init; }
}
