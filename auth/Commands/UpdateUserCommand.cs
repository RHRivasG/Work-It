using AuthenticationService.Contexts;

namespace AuthenticationService.Commands;
class UpdateUserCommand : ICommand
{
    public UserContext UserContext { private get; init; }
    public User User { private get; init; }
    private User? CurrentUser { get; set; } = null;
    public async Task Rollback()
    {
        if (CurrentUser == null) return;

        var user = await UserContext.Users.FindAsync(User.Id);

        if (user == null) return;

        user.Name = CurrentUser.Name;
        user.Password = CurrentUser.Password;
        user.Role = CurrentUser.Role;
        await UserContext.SaveChangesAsync();
    }
    public async Task Run()
    {
        var user = await UserContext.Users.FindAsync(User.Id);
        
        CurrentUser = new User {
            Id = user.Id,
            Name = user.Name,
            Password = user.Password,
            Role = user.Role
        };

        if (user == null) return;

        user.Name = User.Name;
        user.Password = User.Password;
        user.Role = User.Role;
        
        await UserContext.SaveChangesAsync();
    }
}