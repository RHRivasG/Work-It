namespace AuthenticationService.Application.Commands;

public interface ICommand<T> {
    public Task<T> Run();
    public Task Rollback();
}

public interface ICommand {
    public Task Run();
    public Task Rollback();
}