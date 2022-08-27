using AuthenticationService.Application.Interfaces;
using AuthenticationService.Web.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace AuthenticationService.Web.Controllers;

[ApiController]
[Route("[controller]")]
public class AuthorizationController : ControllerBase
{
    [Authorize(AuthenticationSchemes = WorkItTokenAuthenticationHandler.DefaultAuthenticationScheme, Policy = "WorkItPolicy")]
    [HttpGet]
    public Task<ActionResult> Authorize() {
        return Task.FromResult<ActionResult>(Ok());
    }
}