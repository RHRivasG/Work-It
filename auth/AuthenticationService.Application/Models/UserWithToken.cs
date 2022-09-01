using AuthenticationService.Domain.Token;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Models;

public class UserWithToken {
    public User User { get; init; }
    public Token? Token { get; init; }
}
