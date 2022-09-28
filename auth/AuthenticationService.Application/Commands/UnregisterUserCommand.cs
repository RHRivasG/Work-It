using AuthenticationService.Application.Interfaces;

namespace AuthenticationService.Application.Commands;

public class UnregisterUserCommand : ICommand
{
    public IUserRepository UserRepository { get; }
    public Guid Id { get; }

    public UnregisterUserCommand(IUserRepository userRepository, Guid id)
    {
        UserRepository = userRepository;
        Id = id;
    }

    public Task Rollback()
    {
        return Task.CompletedTask;
    }

    public Task Run()
    {
        return UserRepository.DeleteAsync(Id);
    }
}
