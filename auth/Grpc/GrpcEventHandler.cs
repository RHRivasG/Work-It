using AuthenticationService.Models;
using AuthenticationService.Services;
using Grpc.Core;

namespace AuthenticationService.Grpc;

public class GrpcEventHandler : Authenticator.AuthenticatorBase {
    IUseCases UseCases { get; set; }

    public GrpcEventHandler(IUseCases useCases) {
        UseCases = useCases;
    }

    public override async Task<Void> RegisterParticipant(UserInformation request, ServerCallContext context)
    {
        Console.WriteLine("Obtained: " + request);
        if (Guid.TryParse(request.Id, out var guid)) {
            var preferences = request.Preferences.ToArray();
            var name = request.Username;
            var password = request.Password;
            await UseCases.RegisterUser(new (Id: guid, Name: name, Password: password, Role: "participant", Preferences: preferences));
        }
        return new Void();
    }
    public override async Task<Void> UpdateUser(CompleteUserInformation request, ServerCallContext context) {
        Console.WriteLine("Obtained: " + request);
        if (Guid.TryParse(request.Information.Id, out var guid)) {
            var preferences = request.Information.Preferences.ToArray();
            var name = request.Information.Username;
            var password = request.Information.Password;
            await UseCases.UpdateUser(new (Id: guid, Name: name, Password: password, Role: request.Role.Value, Preferences: preferences));
        }
        return new Void();
    }
    public override async Task<Void> SetRoleToTrainer(ChangeRole request, ServerCallContext context)
    {
        if (Guid.TryParse(request.PreviousId.Id, out var guid))
            if (Guid.TryParse(request.CurrentId.Id, out var newGuid))
                await UseCases.SetRoleToTrainer(new (Id: guid, NewId: newGuid));
        
        return new Void();
    }
    public override async Task<Void> UnregisterUser(UserId request, ServerCallContext context) {
        if (Guid.TryParse(request.Id, out var guid))
            await UseCases.UnregisterUser(new (Id: guid));
        
        return new Void();
    }
}