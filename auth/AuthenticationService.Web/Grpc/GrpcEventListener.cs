using System.Text.Json;
using AuthenticationService.Application.Interfaces;
using Grpc.Core;

namespace AuthenticationService.Web.Grpc;

public class GrpcEventListener : Authenticator.AuthenticatorBase
{
    public IUseCases UseCases { get; }

    public GrpcEventListener(IUseCases useCases)
    {
        UseCases = useCases;
    }

    public override Task<Void> RegisterParticipant(UserInformation information, ServerCallContext context)
    {
        return RunCommand(UseCases.RegisterUser(new()
        {
            Id = Guid.Parse(information.Id),
            Name = information.Username,
            Password = information.Password,
            Preferences = information.Preferences.ToArray(),
            Role = new[] { "participant" }
        }));
    }

    public override Task<Void> SetRoleToTrainer(ChangeRole changeRole, ServerCallContext context)
    {
        return RunCommand(UseCases.SetRoleToTrainer(new() { OldId = Guid.Parse(changeRole.PreviousId.Id), NewId = Guid.Parse(changeRole.CurrentId.Id) }));
    }

    public override Task<Void> UnregisterUser(UserId id, ServerCallContext context)
    {
        return RunCommand(UseCases.UnregisterUser(new() { Id = Guid.Parse(id.Id) }));
    }

    public override Task<Void> UpdateUser(CompleteUserInformation information, ServerCallContext context)
    {
        return RunCommand(UseCases.UpdateUser(new()
        {
            Id = Guid.Parse(information.Information.Id),
            Name = information.Information.Username,
            Password = information.Information.Password,
            Preferences = information.Information.Preferences.ToArray(),
            Role = information.Role.Value
        }));
    }

    private async Task<Void> RunCommand(ICommand command)
    {
        await command.Run();

        return new Void();
    }
}
