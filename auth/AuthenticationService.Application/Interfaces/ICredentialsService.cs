using AuthenticationService.Domain.Token;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Interfaces;

public interface ICredentialsService
{
    Task<Token> TokenForAsync(in User user);
    ITokenUserRetrieval GetTokenAsync(byte[] hash);
    Task SaveTokenAsync(in Token token);
    Task RemoveTokenAsync(in Token token);
}
