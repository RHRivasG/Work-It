using AuthenticationService.Contexts;
using AuthenticationService.Services;
using Microsoft.EntityFrameworkCore;
using AspNetCore.RouteAnalyzer;
using AuthenticationService.Grpc;
using System.Net;
using System.Security.Cryptography.X509Certificates;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
// Enable GRPC
builder.Services.AddGrpc();
builder.Services.AddGrpcReflection();
// Add DbContext
builder.Services.AddDbContext<UserContext>(options => {
    options.UseSqlite(builder.Configuration.GetConnectionString("Sqlite"));
});
// Add CORS Default Policy
builder.Services.AddCors(options => {
    options.AddDefaultPolicy(policy => {
        policy.WithOrigins("http://localhost:4200", "http://*", "https://*");
        policy.AllowAnyHeader();
        policy.AllowAnyMethod();
        policy.AllowCredentials();
    });
});
// Add Services
builder.Services.AddSingleton<ICredentialsService, JwtCredentialsService>();
builder.Services.AddTransient<IUseCases, UseCases>();
builder.Services.AddRouteAnalyzer();
// Add WebAPI Controllers
builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
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
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
    app.MapGrpcReflectionService();
}

// Enable the use of CORS
app.UseCors();
// Enable routing and HTTPS Redirection
app.UseAuthorization();
// Add Grpc Event Handler
app.MapGrpcService<GrpcEventHandler>();
// Add WebAPI Controllers
app.MapControllers();
// Register and unregister to service aggregator
app.UseServiceAggregator();

app.Run();