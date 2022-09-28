using AuthenticationService.Domain.User;
using AuthenticationService.Application.Models;

namespace AuthenticationService.Application.Interfaces;
public interface IUserRetrievalBuilder {
    Task<User> Value { get; }
    Task<UserWithToken> WithToken();
}