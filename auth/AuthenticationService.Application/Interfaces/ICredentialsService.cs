using AuthenticationService.Domain.Token;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Interfaces;

public interface ICredentialsService
{
    Task<Token> TokenForAsync(User user);
    ITokenUserRetrieval GetTokenAsync(byte[] hash);
    Task SaveTokenAsync(Token token);
    Task RemoveTokenAsync(Token token);
}
