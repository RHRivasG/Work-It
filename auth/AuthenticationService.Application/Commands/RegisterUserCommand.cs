using AuthenticationService.Application.Models;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Commands;

class RegisterUserCommand : ICommand {
    private Guid? _savedId = null;
    private IUserRepository UserRepository { get; }
    private ReadOnlyMemory<char> Username { get; }
    private ReadOnlyMemory<char> Password { get; }
    private UserRole? Role { get; }
    private string[] Preferences { get; }
    public RegisterUserCommand(IUserRepository userContext, string[] preferences, UserCredentials credentials)
    {
        UserRepository = userContext;
        Preferences = preferences;
        Username = credentials.Username;
        Password = credentials.Password;
        Role = credentials.Role;
    }
    public async Task Rollback()
    {
        if (!_savedId.HasValue) return;
        
        await UserRepository.DeleteAsync(_savedId.Value);
    }
    public async Task Run() {
        var user = new User(Role ?? UserRole.PARTICIPANT, Username, Password, Preferences);
        await UserRepository.CreateAsync(user);
    }
}
