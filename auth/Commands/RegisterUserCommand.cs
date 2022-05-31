using AuthenticationService.Contexts;

namespace AuthenticationService.Commands;

class RegisterUserCommand : ICommand {
    public UserContext UserContext { private get; init; }
    public User User { private get; init; }
    public async Task Rollback()
    {
        var user = await UserContext.Users.FindAsync(User.Id);
        if (user != null) {
            UserContext.Users.Remove(user);
            await UserContext.SaveChangesAsync();
        }
    }
    public async Task Run() {
        await UserContext.Users.AddAsync(User);
        await UserContext.SaveChangesAsync();
    }
}