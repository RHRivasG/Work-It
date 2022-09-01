using AuthenticationService.Application.Models;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Domain.Token;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Commands;

class AuthenticateAdminCommand : ICommand<Token> {
    private UserCredentials Credentials { get; }
    private string AdminSecret { get; }
    private string AdminUsername { get; }
    private ICredentialsService CredentialsService { get; }
    public AuthenticateAdminCommand(ICredentialsService credentialsService, string adminSecret, string adminUsername, in UserCredentials credentials)
    {
        CredentialsService = credentialsService;
        AdminSecret = adminSecret;
        AdminUsername = adminUsername;
        Credentials = credentials;
    }
    public Task Rollback() {
        return Task.CompletedTask;
    }
    public Task<Token> Run()
    {
        var password = Credentials.Password;

        if (!password.Span.SequenceEqual(AdminSecret))
            throw new InvalidPasswordException(password);

        var username = Credentials.Username;

        if (!username.Span.SequenceEqual(AdminUsername))
            throw new InvalidUsernameException(username);

        var user = new User(Credentials.Role ?? UserRole.ADMIN, username, password, Array.Empty<string>());

        return CredentialsService.TokenForAsync(user);
    }
}