using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authentication;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Swashbuckle.AspNetCore.Annotations;
using Microsoft.Net.Http.Headers;

namespace AuthenticationService.Web.Controllers;

[ApiController]
[Route("[controller]")]
[Authorize]
public class AuthController : ControllerBase
{
    [AllowAnonymous]
    [HttpPost]
    [Route("login")]
    [SwaggerOperation(
                Summary = "Logs in a participant, trainer or admin",
                Description = "Requires both HTTP Baisc Authorization Header with the credentials and a X-Role Header with the client role",
                OperationId = "LogIn",
                Tags = new[] { "Authentication" }
            )]
    [SwaggerResponse(200, "User logged in successfully")]
    [SwaggerResponse(401, "Provided credentials or role invalid")]
    [SwaggerResponse(400, "Missing credentials")]
    public async Task<ActionResult> Login(
            [FromHeader(Name = "X-Role"), SwaggerParameter("Role of the user trying to log in")] string role,
            [FromHeader(Name = nameof(HeaderNames.Authorization)), SwaggerParameter("Credentials of the user trying to log in")] string credentials)
    {
        if (HttpContext.User.Identity is { IsAuthenticated: true }) return Ok(); 

        var claims = new List<Claim> {
            new(ClaimTypes.Sid, Request.Headers.Authorization),
            new(ClaimTypes.Role, role)
        };

        var principal = new ClaimsPrincipal(new ClaimsIdentity(claims));

        await HttpContext.SignInAsync(principal);

        return Ok();
    }
    [HttpPost]
    [Route("logout")]
    [SwaggerOperation(
                Summary = "Logs out a participant, trainer or admin",
                Description = "Requires session cookie",
                OperationId = "LogOut",
                Tags = new[] { "Authentication" }
            )]
    public async Task<ActionResult> Logout()
    {
        await HttpContext.SignOutAsync();

        return Ok();
    }
    [HttpGet]
    [SwaggerOperation(
                Summary = "Determines if a user is authorized to access certain resource",
                Description = "Requires session cookie",
                OperationId = "Authorize",
                Tags = new[] { "Authorization" }
            )]
    public Task<ActionResult> Authorize()
    {
        var token = (from claim in HttpContext.User.Claims
                     where claim.Type == ClaimTypes.Sid
                     select claim.Value).First();

        Response.Headers.Authorization = $"Bearer {token}";

        return Task.FromResult<ActionResult>(Ok());
    }
}
