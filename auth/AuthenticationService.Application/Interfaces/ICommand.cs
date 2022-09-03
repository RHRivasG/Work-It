namespace AuthenticationService.Application.Interfaces;

public interface ICommand
{
  Task Run();
  Task Rollback();
}

public interface ICommand<T> : ICommand
{
  async Task ICommand.Run() {
    var _ = await Run();
  }
  new Task<T> Run(); 
}
