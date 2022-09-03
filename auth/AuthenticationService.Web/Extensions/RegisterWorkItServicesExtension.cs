using AuthenticationService.Web.Contexts;
using AuthenticationService.Web.Models;
using Microsoft.Extensions.Options;
using Microsoft.EntityFrameworkCore;
using AuthenticationService.Web.Contexts.Entities;
using Mapster;
using AuthenticationService.Domain.User;
using AuthenticationService.Domain.Token;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Web.Services;
using AuthenticationService.Application.Services;
using AuthenticationService.Application.Models;

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

        TypeAdapterConfig<User, UserEntity>
            .ForType()
            .MapWith(user => new()
            {
                Id = user.Id,
                Name = user.Username.ToString(),
                Password = user.Password.ToString(),
                Role = user.Role.ToArray(),
                Preferences = user.Preferences
            })
            .Compile();

        TypeAdapterConfig<UserEntity, User>
            .ForType()
            .MapWith(entity => new(entity.Role.ToRole(), entity.Name.AsMemory(), entity.Password.AsMemory(), entity.Preferences, entity.Id))
            .Compile();

        TypeAdapterConfig<Token, TokenEntity>
            .ForType()
            .MapWith(token => new()
            {
                Id = token.Id,
                OwnerId = token.OwnerId,
                Value = token.Inner.Value.ToString(),
                ExpiresIn = token.ExpiresIn.Value,
                IssuedAt = token.IssuedAt.Value,
                Hash = token.Hash.Value.ToArray()
            })
            .Compile();

        TypeAdapterConfig<TokenEntity, Token>
            .ForType()
            .MapWith(entity => new(entity.Value, entity.OwnerId, entity.IssuedAt, entity.ExpiresIn, entity.Id, entity.Hash))
            .Compile();

        TypeAdapterConfig<UserEntity, UserWithToken>
            .ForType()
            .MapWith(entity => new UserWithToken(entity.Adapt<User>(), entity.Token == null ? null : entity.Token.Adapt<Token>()))
            .Compile();

        services.AddSingleton<IUserRepository, UserContext>(services => services.GetRequiredService<UserContext>());
        services.AddScoped<ICredentialsService, JwtCredentialsService>();
        services.AddScoped<IUseCases, UseCases>(services =>
        {
            return new UseCases(
                    services.GetRequiredService<ICredentialsService>(),
                    services.GetRequiredService<IUserRepository>(),
                    services.GetRequiredService<IOptions<AdminOptions>>().Value);
        });
    }
}
