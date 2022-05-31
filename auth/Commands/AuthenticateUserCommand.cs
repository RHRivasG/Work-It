using AuthenticationService.Contexts;
using AuthenticationService.Services;
using Microsoft.EntityFrameworkCore;

namespace AuthenticationService.Commands;

public class AuthenticateUserCommand : ICommand<string?> {
    public string Name { private get; init; }
    public string Password { private get; init; }
    public string Role { private get; init; }
    public UserContext UserContext { private get; init; }
    public ICredentialsService CredentialsService { private get; init; }
    public async Task Rollback() {}
    public async Task<string?> Run()
    {
        var user = await UserContext.Users.Where(user => user.Name == Name && user.Password == Password && user.Role == Role).SingleAsync();

        if (user == null) return null;

        return await CredentialsService.CredentialsFor(
            id: user.Name == "admin" ? "admin" : user.Id.ToString(), 
            name: user.Name, 
            preferences: user.Preferences, 
            roles: user.AvailableRoles
        );
    }
}