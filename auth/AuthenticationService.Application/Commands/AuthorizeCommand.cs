using AuthenticationService.Application.Commands;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Domain.Token;

public class AuthorizeCommand : ICommand<Token>
{
    public ICredentialsService CredentialsService { get; }
    public byte[] Hash { get; }
    public AuthorizeCommand(ICredentialsService service, byte[] hash)
    {
        CredentialsService = service;
        Hash = hash;
    }
    public Task<Token> Run()
    {
        return CredentialsService.GetTokenAsync(Hash); 
    }
    public Task Rollback() => Task.CompletedTask;
}