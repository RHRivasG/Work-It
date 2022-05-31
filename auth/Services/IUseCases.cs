using AuthenticationService.Models;

namespace AuthenticationService.Services;

public interface IUseCases {
    Task RegisterUser(RegisterUserForm Form); 
    Task UpdateUser(UpdateUserForm Form); 
    Task SetRoleToTrainer(SetRoleToTrainerForm Form);
    Task UnregisterUser(UnregisterUserForm Form); 
    Task<string?> AuthenticateUser(AuthenticateUserForm Form); 
    Task<string?> AuthenticateAdmin(AuthenticateAdminForm Form); 
}