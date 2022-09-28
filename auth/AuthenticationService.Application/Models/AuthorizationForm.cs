namespace AuthenticationService.Application.Models;

public readonly record struct AuthorizationForm
{
  public byte[] Hash { get; init; }
}
