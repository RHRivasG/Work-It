namespace AuthenticationService.Commands;

interface ICommand<T> {
    public Task<T> Run();
    public Task Rollback();
}

interface ICommand {
    public Task Run();
    public Task Rollback();
}