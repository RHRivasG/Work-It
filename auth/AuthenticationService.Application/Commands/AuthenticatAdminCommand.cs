using AuthenticationService.Application.Models;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Domain.Token;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Commands;

class AuthenticateAdminCommand : ICommand<Token> {
    private ReadOnlyMemory<char> Username { get; }
    private ReadOnlyMemory<char> Password { get; }
    private UserRole? Role { get; }
    private string AdminSecret { get; }
    private string AdminUsername { get; }
    private ICredentialsService CredentialsService { get; }
    public AuthenticateAdminCommand(ICredentialsService credentialsService, string adminSecret, string adminUsername, UserCredentials credentials)
    {
        CredentialsService = credentialsService;
        AdminSecret = adminSecret;
        AdminUsername = adminUsername;
        Username = credentials.Username;
        Password = credentials.Password;
        Role = credentials.Role;
    }
    public Task Rollback() {
        return Task.CompletedTask;
    }
    public Task<Token> Run()
    {
        if (!Password.Span.SequenceEqual(AdminSecret))
            throw new InvalidPasswordException(Password);

        if (!Username.Span.SequenceEqual(AdminUsername))
            throw new InvalidUsernameException(Username);

        var user = new User(Role ?? UserRole.ADMIN, Username, Password, Array.Empty<string>());

        return CredentialsService.TokenForAsync(user);
    }
}
