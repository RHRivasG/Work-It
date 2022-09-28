using AuthenticationService.Application.Models;
using AuthenticationService.Domain.Token;

namespace AuthenticationService.Application.Interfaces;

public interface ITokenUserRetrieval
{
  ValueTask<Token> Value { get; }
  ValueTask<UserWithToken> WithUser();
}
