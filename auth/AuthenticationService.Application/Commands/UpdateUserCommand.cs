using AuthenticationService.Application.Models;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Commands;
class UpdateUserCommand : ICommand
{
    private IUserRepository UserRepository { get; }
    private Guid? _id;
    private UserCredentials NewCredentials { get; }
    private string[] Preferences { get; }
    private User? CurrentUser { get; set; } = null;
    public UpdateUserCommand(IUserRepository userRepository, Guid id, string[] preferences, UserCredentials newCredentials)
    {
        UserRepository = userRepository;
        Preferences = preferences;
        _id = id;
        NewCredentials = newCredentials;
    }
    public async Task Rollback()
    {
        if (!CurrentUser.HasValue) return;

        await UserRepository.SaveAsync(CurrentUser.Value);
    }
    public async Task Run()
    {       
        if (!_id.HasValue) return;

        var user = await UserRepository.FindAsync(_id.Value);
        CurrentUser = user;

        user.Username = NewCredentials.GetUsername();
        user.Password = NewCredentials.GetPassword();
        user.Preferences = Preferences;

        await UserRepository.SaveAsync(user);
    }
}