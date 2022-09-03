using System.IdentityModel.Tokens.Jwt;
using System.Runtime.InteropServices;
using System.Security.Claims;
using System.Text;
using System.Text.Json;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Application.Models;
using AuthenticationService.Domain.Token;
using AuthenticationService.Domain.User;
using AuthenticationService.Web.Contexts;
using AuthenticationService.Web.Contexts.Entities;
using Mapster;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;

namespace AuthenticationService.Web.Services;

public class JwtCredentialsService : ICredentialsService, ITokenUserRetrieval
{
    private string SecretKey { get; set; }
    private TimeSpan Expiration { get; set; }
    private UserContext UserContext { get; set; }
    private byte[]? SearchHash { get; set; }
    public JwtCredentialsService(IConfiguration configuration, IOptionsMonitor<AuthenticationOptions> options, UserContext tokenContext)
    {
        SecretKey = configuration["Jwt:Key"];

        if (!options.CurrentValue.ExpiresIn.HasValue)
            throw new InvalidAuthenticationOptionsException(nameof(options.CurrentValue.ExpiresIn));

        Expiration = options.CurrentValue.ExpiresIn.Value;
        UserContext = tokenContext;
    }
    public Task<Token> TokenForAsync(User user)
    {
        var symmetricKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(SecretKey));
        var credentials = new SigningCredentials(symmetricKey, SecurityAlgorithms.HmacSha512);

        var claims = new List<Claim> {
            new ("sub", user.Role is UserRole.ADMIN ? "admin" : user.Id.ToString()),
            new ("roles", JsonSerializer.Serialize(user.Role.ToArray()), JsonClaimValueTypes.JsonArray),
            new ("preferences", JsonSerializer.Serialize(user.Preferences), JsonClaimValueTypes.JsonArray)
        };

        var jwt = new JwtSecurityToken(signingCredentials: credentials, claims: claims);
        var tokenValue = new JwtSecurityTokenHandler().WriteToken(jwt);

        var token = new Token(tokenValue, user.Id, DateTime.Now, Expiration);

        return Task.FromResult(token);
    }
    public ITokenUserRetrieval GetTokenAsync(byte[] hash)
    {
        SearchHash = hash;
        return this;
    }
    public Task RemoveTokenAsync(Token token)
    {
        if (!MemoryMarshal.TryGetArray(token.Hash.Value, out var hashSegment) || hashSegment.Array is null)
            throw new MemoryAccessException("Failed to access hash byte array");

        if (!MemoryMarshal.TryGetString(token.Inner.Value, out var tokenValue, out _, out _) || tokenValue is null)
            throw new MemoryAccessException("Failed to access hash byte array");

        UserContext.Tokens.Remove(token.Adapt<TokenEntity>());

        return UserContext.SaveChangesAsync();
    }
    public async ValueTask<UserWithToken> WithUser()
    {
        var token = await FindTokenEntity()
            .Join(
                UserContext.Users, 
                token => token.OwnerId,
                user => user.Id,
                (token, user) => new UserWithToken(user.Adapt<User>(), token.Adapt<Token>())
            )
            .FirstAsync();
        return token;
    }
    public ValueTask<Token> Value { get => FindToken(); }
    public Task SaveTokenAsync(Token token)
    {
        if (!MemoryMarshal.TryGetArray(token.Hash.Value, out var hashSegment) || hashSegment.Array is null)
            throw new MemoryAccessException("Failed to access hash byte array");

        if (!MemoryMarshal.TryGetString(token.Inner.Value, out var tokenValue, out _, out _) || tokenValue is null)
            throw new MemoryAccessException("Failed to access hash byte array");

        return AddTokenAsync(token.Adapt<TokenEntity>());
    }
    private async Task AddTokenAsync(TokenEntity entity)
    {
        var userEntity = await UserContext.Users.Where(user => user.Id == entity.OwnerId).FirstAsync();

        await UserContext.Tokens.AddAsync(entity);

        userEntity.Token = entity;

        await UserContext.SaveChangesAsync();
    }
    private IQueryable<TokenEntity> FindTokenEntity() => UserContext.Tokens.Where(token => token.Hash == SearchHash);
    private async ValueTask<Token> FindToken()
    {
        if (SearchHash is null)
#warning TODO: Implement invalid hash exception
            throw new Exception("Hash not set");

        return (await FindTokenEntity().FirstAsync()).Adapt<Token>();
    }
}
