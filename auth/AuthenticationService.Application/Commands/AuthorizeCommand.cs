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
        return await CredentialsService.GetTokenAsync(Hash).Value; 
    }
    public Task Rollback() => Task.CompletedTask;
}
