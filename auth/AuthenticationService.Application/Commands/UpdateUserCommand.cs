using AuthenticationService.Application.Models;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Commands;
class UpdateUserCommand : ICommand
{
    private IUserRepository UserRepository { get; }
    private Guid? _id;
    private ReadOnlyMemory<char> NewUsername { get; }
    private ReadOnlyMemory<char> NewPassword { get; }
    private UserRole? NewRole { get; }
    private string[] NewPreferences { get; }
    private User? CurrentUser { get; set; } = null;

    public UpdateUserCommand(IUserRepository userRepository, Guid id, string[] preferences, UserCredentials newCredentials)
    {
        UserRepository = userRepository;
        _id = id;
        NewUsername = newCredentials.Username;
        NewPassword = newCredentials.Password;
        NewRole = newCredentials.Role;
        NewPreferences = preferences;
    }
    public async Task Rollback()
    {
        if (CurrentUser is null) return;

        await UserRepository.SaveAsync(CurrentUser);
    }
    public async Task Run()
    {       
        if (!_id.HasValue) return;

        var user = await UserRepository.FindAsync(_id.Value).Value;

        CurrentUser = user;

        user.Username = NewUsername;
        user.Password = NewPassword;
        user.Preferences = NewPreferences;

        await UserRepository.SaveAsync(user);
    }
}
