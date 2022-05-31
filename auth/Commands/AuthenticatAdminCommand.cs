using AuthenticationService.Services;

namespace AuthenticationService.Commands;

class AuthenticateAdminCommand : ICommand<string?> {
    public string Password { private get; init; }
    public string AdminSecret  { private get; init; }
    public ICredentialsService CredentialsService { private get; init; }
    public async Task Rollback() {}
    public Task<string?> Run()
    {
        if (Password != AdminSecret) return Task.FromResult<string?>(null);
        return CredentialsService.CredentialsFor(
                id: "admin",
                name: "admin",
                preferences: new string[] {},
                roles: new [] { "admin"}
        );
    }
}