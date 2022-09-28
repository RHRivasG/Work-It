using AuthenticationService.Application.Models;
using AuthenticationService.Domain.Token;
using AuthenticationService.Domain.User;
using AuthenticationService.Web.Contexts.Entities;

namespace AuthenticationService.Web.Extensions;

public static class MappingExtensions
{
    public static UserEntity ToEntity(this User user) {
        return new UserEntity {
            Id = user.Id,
            Name = user.Username.ToString(),
            Password = user.Password.ToString(),
            Role = user.Role.ToArray(),
            Preferences = user.Preferences
        };
    }
    public static TokenEntity ToEntity(this Token token) {
        return new TokenEntity {
            Id = token.Id,
            OwnerId = token.OwnerId,
            Hash = token.Hash.Value.ToArray(),
            ExpiresIn = token.ExpiresIn.Value,
            IssuedAt = token.IssuedAt.Value,
            Value = token.Inner.Value.ToString()
        };
    }
    public static User ToUser(this UserEntity userEntity) {
        return new User(userEntity.Role.ToRole(), userEntity.Name.AsMemory(), userEntity.Password.AsMemory(), userEntity.Preferences, userEntity.Id);
    }
    public static Token ToToken(this TokenEntity tokenEntity) {
        return new Token(tokenEntity.Value, tokenEntity.OwnerId, tokenEntity.IssuedAt, tokenEntity.ExpiresIn, tokenEntity.Id, tokenEntity.Hash);
    }
    public static UserWithToken ToUserWithToken(this UserEntity user) {
        return new UserWithToken(user.ToUser(), user.Token?.ToToken());
    }
}