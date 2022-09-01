using System.Text;

namespace AuthenticationService.Web.Middelwares;

public class ErrorHandlerMiddleware
{
    RequestDelegate _next;

    public ErrorHandlerMiddleware(RequestDelegate next)
    {
        _next = next;
    }

    public async Task InvokeAsync(HttpContext context) {
        try {
            await _next(context);
        } catch(InvalidUsernameException err) {
            context.Response.StatusCode = StatusCodes.Status401Unauthorized;
            await context.Response.Body.WriteAsync(Encoding.UTF8.GetBytes(err.Message));
        } catch(InvalidPasswordException err) {
            context.Response.StatusCode = StatusCodes.Status401Unauthorized;
            await context.Response.Body.WriteAsync(Encoding.UTF8.GetBytes(err.Message));
        } catch(InvalidUserRoleException err) {
            context.Response.StatusCode = StatusCodes.Status401Unauthorized;
            await context.Response.Body.WriteAsync(Encoding.UTF8.GetBytes(err.Message));
        } catch(MemoryAccessException err) {
            context.Response.StatusCode = StatusCodes.Status503ServiceUnavailable;
            await context.Response.Body.WriteAsync(Encoding.UTF8.GetBytes(err.Message));
        }  catch(Exception err) {
            context.Response.StatusCode = StatusCodes.Status500InternalServerError;
            await Console.Error.WriteLineAsync(err.ToString());
            await context.Response.Body.WriteAsync(Encoding.UTF8.GetBytes(err.Message));
        }
    }
}
