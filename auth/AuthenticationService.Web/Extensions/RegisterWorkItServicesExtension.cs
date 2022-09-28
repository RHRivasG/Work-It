using AuthenticationService.Web.Contexts;
using AuthenticationService.Web.Models;
using Microsoft.Extensions.Options;
using Microsoft.EntityFrameworkCore;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Web.Services;
using AuthenticationService.Application.Services;

namespace AuthenticationService.Web.Extensions;

public static class RegisterWorkItServicesExtension
{
    public static void AddWorkItServices(this IServiceCollection services, Action<WorkItServicesOptions> confOptions)
    {
        services
            .AddOptions<WorkItServicesOptions>()
            .Configure(confOptions);

        services
            .AddOptions<AdminOptions>()
            .Configure<IOptions<WorkItServicesOptions>>((adm, wit) =>
            {
                adm.AdminUsername = wit.Value.Admin.AdminUsername;
                adm.AdminSecret = wit.Value.Admin.AdminSecret;
            });

        services
            .AddOptions<AuthenticationOptions>()
            .Configure<IOptions<WorkItServicesOptions>>((auth, wit) =>
            {
                auth.ExpiresIn = wit.Value.Authentication.ExpiresIn;
            });


        services
            .AddDbContextPool<UserContext>((srv, options) =>
            {
                var serviceOptions = srv.GetRequiredService<IOptionsMonitor<WorkItServicesOptions>>();
                options.UseSqlite(serviceOptions.CurrentValue.ConnectionString);
            });


        services.AddScoped<IUserRepository, UserContext>(services => services.GetRequiredService<UserContext>());
        services.AddTransient<ICredentialsService, JwtCredentialsService>();
        services.AddTransient<IUseCases, UseCases>(services =>
        {
            return new UseCases(
                    services.GetRequiredService<ICredentialsService>(),
                    services.GetRequiredService<IUserRepository>(),
                    services.GetRequiredService<IOptions<AdminOptions>>().Value);
        });
    }
}
