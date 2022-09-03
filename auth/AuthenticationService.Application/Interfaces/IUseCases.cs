using AuthenticationService.Application.Models;
using AuthenticationService.Domain.Token;

namespace AuthenticationService.Application.Interfaces;

public interface IUseCases {
  ICommand<Token> AuthenticateUser(UserCredentials credentials); 
  ICommand<Token> AuthenticateAdmin(UserCredentials credentials);
  ICommand<Token> Authorize(AuthorizationForm form);
  ICommand RegisterUser(RegisterUserForm form);
  ICommand SetRoleToTrainer(SetRoleToTrainerForm form);
  ICommand UpdateUser(UpdateUserForm form);
  ICommand UnregisterUser(UnregisterUserForm form);
}
