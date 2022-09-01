using Microsoft.EntityFrameworkCore;
using AspNetCore.RouteAnalyzer;
using AuthenticationService.Web.Grpc;
using System.Net;
using System.Security.Cryptography.X509Certificates;
using AuthenticationService.Web.Extensions;
using Microsoft.OpenApi.Models;
using Microsoft.Net.Http.Headers;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
// Enable GRPC
builder.Services.AddGrpc();
builder.Services.AddGrpcReflection();
// Add CORS Default Policy
builder.Services.AddCors(options => {
    options.AddDefaultPolicy(policy => {
        policy.WithOrigins("http://localhost:4200", "http://*", "https://*");
        policy.AllowAnyHeader();
        policy.AllowAnyMethod();
        policy.AllowCredentials();
    });
});
// Add WorkIt Services
builder.Services.AddWorkItServices(options => {
    options.ConnectionString = builder.Configuration.GetConnectionString("Sqlite");
    options.Admin.AdminSecret = "admin-secret";
    options.Admin.AdminUsername = "admin";
    options.Authentication.ExpiresIn = TimeSpan.FromMinutes(5);
});
builder.Services.AddRouteAnalyzer();
// Add Auhtentication/Authorization Handlers
builder.Services.AddWorkItAuth();
// Add WebAPI Controllers
builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(options => {
            options.EnableAnnotations();
            options.AddSecurityDefinition(
                    "login",
                    new OpenApiSecurityScheme {
                            Type = SecuritySchemeType.Http,
                            Scheme = "Basic",
                            In = ParameterLocation.Header,
                            Name = HeaderNames.Authorization
                        }
                    );
            options.AddSecurityRequirement(
                    new OpenApiSecurityRequirement 
                    {
                        {
                            new OpenApiSecurityScheme 
                            {
                                Reference = new OpenApiReference 
                                {
                                    Type = ReferenceType.SecurityScheme,
                                    Id = "login"
                                }
                            },
                            Array.Empty<string>()
                        }
                    });
        });
// Enable Multiple URLS
builder.WebHost
    .UseKestrel()
    .ConfigureKestrel(options => {
        var webApiPort = int.Parse(builder.Configuration["WebApi:Port"]);
        var grpcPort = int.Parse(builder.Configuration["gRPC:Port"]);
        var bindAddress = IPAddress.Parse("0.0.0.0");
        options.ConfigureHttpsDefaults(options => {
            var certPath = builder.Configuration["Certificate:Path"];
            var keyPath = builder.Configuration["Certificate:KeyPath"];
            var certContents = File.ReadAllText(certPath);
            var keyContents = File.ReadAllText(keyPath);
            options.ServerCertificate = X509Certificate2.CreateFromPem(certContents, keyContents);
        });
        options.Listen(bindAddress, webApiPort, o => o.Protocols = Microsoft.AspNetCore.Server.Kestrel.Core.HttpProtocols.Http1);
        options.Listen(bindAddress, grpcPort, o => {
            o.Protocols = Microsoft.AspNetCore.Server.Kestrel.Core.HttpProtocols.Http2;
            o.UseHttps();
        });
    });

var app = builder.Build();
// Configure the HTTP request pipeline.
app.UseWorkItExceptionHandler();
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
    app.MapGrpcReflectionService();
}

// Enable the use of CORS
app.UseCors();
// Enable Routing
app.UseRouting();
// Enable Authentication and Authroization
app.UseAuthentication();
app.UseAuthorization();
// Enable endpoints for gRPC and HTTP
app.UseEndpoints(endpoints => {
    // Add WebAPI Controllers
    endpoints.MapControllers();
    // Add Grpc Event Handler
    endpoints.MapGrpcService<GrpcEventHandler>();
});
// Register and unregister to service aggregator
// app.UseServiceAggregator();

app.Run();
