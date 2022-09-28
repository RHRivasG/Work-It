using AuthenticationService.Domain.Token;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Models;

public class UserWithToken {
    public User User { get; }
    public Token? Token { get; }

    public UserWithToken(User user, Token? token = null)
    {
        User = user;
        Token = token;
    }
}
