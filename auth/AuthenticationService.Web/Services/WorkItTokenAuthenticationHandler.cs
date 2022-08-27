using System.Runtime.InteropServices;
using System.Security.Claims;
using System.Text.Encodings.Web;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Web.Models;
using Microsoft.AspNetCore.Authentication;
using Microsoft.Extensions.Options;

namespace AuthenticationService.Web.Services;

public class WorkItTokenAuthenticationHandler : AuthenticationHandler<WorkItTokenSchemeOptions>
{
    public const string DefaultAuthenticationScheme = "WorkItToken";
    IUseCases UseCases { get; }
    public WorkItTokenAuthenticationHandler(
        IOptionsMonitor<WorkItTokenSchemeOptions> options,
        ILoggerFactory logger,
        UrlEncoder encoder,
        ISystemClock clock,
        IUseCases useCases
    ) : base(options, logger, encoder, clock)
    {
        UseCases = useCases;
    }
    protected override async Task<AuthenticateResult> HandleAuthenticateAsync()
    {
        if (Context.User is null or { Identity: { IsAuthenticated: false } }) return AuthenticateResult.Fail("User is not authenticated");

        var tokenHash = (from claim in Context.User.Claims
                     where claim.Type == ClaimTypes.Hash
                     select claim.Value).First();

        var token = await UseCases.Authorize(new() { Hash = Convert.FromHexString(tokenHash) }).Run();

        if (!MemoryMarshal.TryGetString(token.Inner.Value, out var tokenValue, out _, out _))
            return AuthenticateResult.Fail("Failure retrieving token value");

        Response.Headers.Authorization = $"Bearer {tokenValue}";

        if (!token.IsValid) {
            await Context.SignOutAsync(WorkItAuthenticationHandler.DefaultAuthenticationScheme);
            return AuthenticateResult.Fail("The provided token is invalid");
        }

        var claims = new List<Claim>() {
            new(ClaimTypes.NameIdentifier, token.OwnerId.ToString())
        };

        var claimsPrincipal = new ClaimsPrincipal(new ClaimsIdentity(claims));

        return AuthenticateResult.Success(new AuthenticationTicket(claimsPrincipal, new(), Scheme.Name));
    }
}