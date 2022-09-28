namespace AuthenticationService.Domain.Token.Values;

public readonly record struct TokenIssuedAt
{
  public DateTime Value { get; }

  public TokenIssuedAt(DateTime? value = null)
  {
    Value = value ?? DateTime.Now; 
  }
}
