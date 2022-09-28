namespace AuthenticationService.Domain.Token.Values;

public readonly record struct TokenExpiresIn {
  public TimeSpan Value { get; }

  public TokenExpiresIn(TimeSpan? values = null)
  {
    Value = values ?? TimeSpan.FromHours(1); 
  }
}
