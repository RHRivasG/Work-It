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
    public ValueTask<UserWithToken> WithUser()
    {
        var token = FindToken();
    }
    public ValueTask<Token> Value { get => FindToken(); }
    public JwtCredentialsService(IConfiguration configuration, IOptionsMonitor<AuthenticationOptions> options, UserContext tokenContext)
    {
        SecretKey = configuration["Jwt:Key"];
        if (!options.CurrentValue.ExpiresIn.HasValue)
            throw new AuthenticationOptionsNotProvidedException("Expiration value not provided");

        Expiration = options.CurrentValue.ExpiresIn.Value;
        UserContext = tokenContext;
    }
    public Task<Token> TokenForAsync(in User user)
    {
        var symmetricKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(SecretKey));
        var credentials = new SigningCredentials(symmetricKey, SecurityAlgorithms.HmacSha512);

        var claims = new List<Claim> {
            new Claim("sub", user.Role is UserRole.ADMIN ? "admin" : user.Id.ToString()),
            new Claim("roles", JsonSerializer.Serialize(user.Role.ToArray()), JsonClaimValueTypes.JsonArray),
            new Claim("preferences", JsonSerializer.Serialize(user.Preferences), JsonClaimValueTypes.JsonArray)
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
    public Task RemoveTokenAsync(in Token token)
    {
        if (!MemoryMarshal.TryGetArray(token.Hash.Value, out var hashSegment) || hashSegment.Array is null)
            throw new MemoryAccessException("Failed to access hash byte array");

        if (!MemoryMarshal.TryGetString(token.Inner.Value, out var tokenValue, out _, out _) || tokenValue is null)
            throw new MemoryAccessException("Failed to access hash byte array");

        var entity = new TokenEntity()
        {
            Id = token.Id,
            OwnerId = token.OwnerId,
            Value = tokenValue,
            Hash = hashSegment.Array,
            ExpiresIn = token.ExpiresIn.Value,
            IssuedAt = token.IssuedAt.Value
        };

        UserContext.Tokens.Remove(entity);

        return UserContext.SaveChangesAsync();
    }
    public Task SaveTokenAsync(in Token token)
    {
        if (!MemoryMarshal.TryGetArray(token.Hash.Value, out var hashSegment) || hashSegment.Array is null)
            throw new MemoryAccessException("Failed to access hash byte array");

        if (!MemoryMarshal.TryGetString(token.Inner.Value, out var tokenValue, out _, out _) || tokenValue is null)
            throw new MemoryAccessException("Failed to access hash byte array");

        var entity = new TokenEntity()
        {
            Id = token.Id,
            OwnerId = token.OwnerId,
            Value = tokenValue,
            Hash = hashSegment.Array,
            ExpiresIn = token.ExpiresIn.Value,
            IssuedAt = token.IssuedAt.Value
        };
        return AddTokenAsync(entity);
    }
    private async Task AddTokenAsync(TokenEntity entity)
    {
        var userEntity = await UserContext.Users.Where(user => user.Id == entity.OwnerId).FirstAsync();

        await UserContext.Tokens.AddAsync(entity);

        userEntity.Token = entity;

        await UserContext.SaveChangesAsync();
    }
    private async ValueTask<TokenEntity> FindTokenEntity() {
        return await UserContext.Tokens.Where(token => token.Hash == SearchHash).FirstAsync();
    }
    private async ValueTask<Token> FindToken()
    {
        if (SearchHash is null)
#warning TODO: Implement invalid hash exception
            throw new Exception("Hash not set");
        var tokenEntity = await FindTokenEntity();

        return new(tokenEntity.Value, tokenEntity.OwnerId, tokenEntity.IssuedAt, tokenEntity.ExpiresIn, tokenEntity.Id);
    }
}
