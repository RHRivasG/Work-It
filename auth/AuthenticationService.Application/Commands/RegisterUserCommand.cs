
using AuthenticationService.Application.Models;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Commands;

class RegisterUserCommand : ICommand {
    private Guid? _savedId = null;
    private IUserRepository UserRepository { get; }
    private UserCredentials Credentials { get; }
    private string[] Preferences { get; }
    public RegisterUserCommand(IUserRepository userContext, string[] preferences, in UserCredentials credentials)
    {
        UserRepository = userContext;
        Credentials = credentials;
        Preferences = preferences;
    }
    public async Task Rollback()
    {
        if (!_savedId.HasValue) return;
        
        await UserRepository.DeleteAsync(_savedId.Value);
    }
    public async Task Run() {
        var user = new User(Credentials.Role ?? UserRole.PARTICIPANT, Credentials.GetUsername(), Credentials.GetPassword(), Preferences);
        await UserRepository.CreateAsync(user);
    }
}