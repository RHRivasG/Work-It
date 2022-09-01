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
    public async Task<Token> Run()
    {
        var token = await CredentialsService.GetTokenAsync(Hash); 
        token.AssertValid();
        return token;
    }
    public Task Rollback() => Task.CompletedTask;
}
