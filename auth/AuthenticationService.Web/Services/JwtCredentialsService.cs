using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using System.Text.Json;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Domain.Token;
using AuthenticationService.Domain.User;
using Microsoft.IdentityModel.Tokens;

namespace AuthenticationService.Web.Services;

public class JwtCredentialsService : ICredentialsService {
    private string SecretKey { get; set; }
    private TimeSpan Expiration { get; set; }
    private TokenContext TokenContext { get; set; }
    public JwtCredentialsService(IConfiguration configuration, AuthenticationOptions options, TokenContext tokenContext)
    {
        SecretKey = configuration["Jwt:Key"];
        if (!options.ExpiresIn.HasValue)
            throw new AuthenticationOptionsNotProvidedException("Expiration value not provided");

        Expiration = options.ExpiresIn.Value;
        TokenContext = tokenContext;
    }
    public Task<Token> TokenForAsync(in User user)
    {
        var symmetricKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(SecretKey));
        var credentials = new SigningCredentials(symmetricKey, SecurityAlgorithms.HmacSha512);
        
        var claims = new List<Claim> {
            new Claim("sub", user.Username == "admin" ? "admin" : user.Id.ToString()),
            new Claim("roles", JsonSerializer.Serialize(user.Role.ToArray()), JsonClaimValueTypes.JsonArray),
            new Claim("preferences", JsonSerializer.Serialize(user.Preferences), JsonClaimValueTypes.JsonArray)
        };
        
        var jwt = new JwtSecurityToken(signingCredentials: credentials, claims: claims);
        var tokenValue = new JwtSecurityTokenHandler().WriteToken(jwt);

        var token = new Token(tokenValue, user.Id, DateTime.Now, Expiration);
        
        return Task.FromResult(token);    
    }
    public Task<Token> GetTokenAsync(byte[] hash)
    {
        return TokenContext.FindTokenByHashAsync(hash);
    }
    public Task SaveTokenAsync(in Token token)
    {
        return TokenContext.AddTokenAsync(token);
    }
}