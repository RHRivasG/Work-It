using AuthenticationService.Application.Interfaces;

namespace AuthenticationService.Application.Commands;

public class SetRoleToTrainerCommand : ICommand {
    private IUserRepository UserRepository { get; }
    private Guid OldId { get; }
    private Guid NewId { get; }
    private UserRole? PreviousRole { get; set; } = null;
    public SetRoleToTrainerCommand(IUserRepository userRepository, Guid oldId = default, Guid newId = default)
    {
        UserRepository = userRepository;
        OldId = oldId;
        NewId = newId;
    }
    public async Task Rollback()
    {
        if (!PreviousRole.HasValue) return;

        var user = await UserRepository.FindAsync(OldId);

        if (user.Is(PreviousRole.Value)) return;

        user.Role = PreviousRole.Value;
        
        await UserRepository.SaveAsync(user);    
    }
    public async Task Run()
    {
        var user = await UserRepository.FindAsync(OldId);

        if (!user.Is(UserRole.TRAINER)) return;

        PreviousRole = user.Role;

        user.PromoteToTrainer(NewId);

        await UserRepository.SaveAsync(user);
    }
}