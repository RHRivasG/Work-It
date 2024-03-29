using AuthenticationService.Application.Models;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Domain.Token;

namespace AuthenticationService.Application.Commands;

public class AuthenticateUserCommand : ICommand<Token> {
    private UserCredentials Credentials { get; }
    private IUserRepository Repository { get; }
    private ICredentialsService CredentialsService { get; }
    public AuthenticateUserCommand(IUserRepository repository, ICredentialsService credentialsService, in UserCredentials credentials)
    {
        Credentials = credentials;
        Repository = repository;
        CredentialsService = credentialsService;
    }
    public Task Rollback() => Task.CompletedTask;
    public async Task<Token> Run()
    {
        var user = await Repository.FindAsync(Credentials);
        var token = await CredentialsService.TokenForAsync(user);

        await CredentialsService.SaveTokenAsync(token);

        return token;
    }
}