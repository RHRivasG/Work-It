using AuthenticationService.Application.Interfaces;

namespace AuthenticationService.Application.Commands;

class UnregisterUserCommand: ICommand {
    private IUserRepository Repository { get; }
    private Guid Id { get; }
    public UnregisterUserCommand(IUserRepository repository, Guid id = default)
    {
        Repository = repository;
        Id = id;
    }
    public async Task Rollback()
    {
        await Repository.UndoDeleteAsync(Id);
    }
    public async Task Run()
    {
        await Repository.DeleteAsync(Id);
    }
}