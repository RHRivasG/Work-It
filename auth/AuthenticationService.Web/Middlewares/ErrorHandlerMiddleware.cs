using System.Text;
using Microsoft.EntityFrameworkCore;

namespace AuthenticationService.Web.Middelwares;

public class ErrorHandlerMiddleware
{
    RequestDelegate _next;
    ILogger Logger { get; }

    public ErrorHandlerMiddleware(
            RequestDelegate next,
            ILogger<ErrorHandlerMiddleware> logger
        )
    {
        _next = next;
        Logger = logger;
    }

    public async Task InvokeAsync(HttpContext context) {
        try {
            await _next(context);
        } catch(InvalidUsernameException err) {
            context.Response.StatusCode = StatusCodes.Status401Unauthorized;
            Logger.LogWarning(err.Message);
            await context.Response.Body.WriteAsync(Encoding.UTF8.GetBytes(err.Message));
        } catch(InvalidPasswordException err) {
            context.Response.StatusCode = StatusCodes.Status401Unauthorized;
            Logger.LogWarning(err.Message);
            await context.Response.Body.WriteAsync(Encoding.UTF8.GetBytes(err.Message));
        } catch(InvalidUserRoleException err) {
            context.Response.StatusCode = StatusCodes.Status401Unauthorized;
            Logger.LogWarning(err.Message);
            await context.Response.Body.WriteAsync(Encoding.UTF8.GetBytes(err.Message));
        } catch(MemoryAccessException err) {
            context.Response.StatusCode = StatusCodes.Status503ServiceUnavailable;
            Logger.LogError(err.ToString());
            await context.Response.Body.WriteAsync(Encoding.UTF8.GetBytes(err.Message));
        } catch(DbUpdateException err) {
            context.Response.StatusCode = StatusCodes.Status409Conflict;
            Logger.LogWarning(err.ToString());
            await context.Response.Body.WriteAsync(Encoding.UTF8.GetBytes(err.Message));
        } catch(Exception err) {
            context.Response.StatusCode = StatusCodes.Status500InternalServerError;
            Logger.LogError(err.ToString());
            await context.Response.Body.WriteAsync(Encoding.UTF8.GetBytes(err.Message));
        }
    }
}
