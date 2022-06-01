using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using System.Text.Json;
using Microsoft.IdentityModel.Tokens;

namespace AuthenticationService.Services;

public class JwtCredentialsService : ICredentialsService {
    private string SecretKey { get; set; }
    public JwtCredentialsService(IConfiguration configuration) {
        SecretKey = configuration["Jwt:Key"];
    }
    public Task<string> CredentialsFor(string id, string name, string[] preferences, string[] roles)
    {
        var symmetricKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(SecretKey));
        var credentials = new SigningCredentials(symmetricKey, SecurityAlgorithms.HmacSha512);
        
        var claims = new List<Claim> {
            new Claim("sub", name == "admin" ? "admin" : id),
            new Claim("roles", JsonSerializer.Serialize(roles), JsonClaimValueTypes.JsonArray),
            new Claim("preferences", JsonSerializer.Serialize(preferences), JsonClaimValueTypes.JsonArray)
        };
        
        var token = new JwtSecurityToken(signingCredentials: credentials, claims: claims);
        
        return Task.FromResult(new JwtSecurityTokenHandler().WriteToken(token));
    }
}