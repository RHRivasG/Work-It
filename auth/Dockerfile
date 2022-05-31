FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build-env
WORKDIR /app

# Copy csproj and restore as distinct layers
COPY auth/*.csproj /app/server/
WORKDIR /app/server
RUN dotnet restore

# Copy .config and restore the dotnet tools
COPY auth/.config /app/server/.config
RUN dotnet tool restore

# Copy everything else and build
COPY protobuf/ /app/protobuf
COPY auth/Commands /app/server/
COPY auth/Contexts /app/server/
COPY auth/Controllers /app/server/
COPY auth/Grpc /app/server/
COPY auth/Migrations /app/server/
COPY auth/Models /app/server/
COPY auth/Services /app/server/
COPY auth/Program.cs /app/server/
COPY auth/WeatherForecast.cs /app/server/
COPY auth/appsettings.Production.json /app/server/appsettings.json
WORKDIR /app/server
RUN dotnet publish -c Release -o out ./AuthenticationService.csproj
RUN dotnet ef database update

# Build runtime image
FROM mcr.microsoft.com/dotnet/aspnet:6.0
WORKDIR /app
COPY --from=build-env /app/server/out .
COPY certs/ /app/certs
ENTRYPOINT ["dotnet", "AuthenticationService.dll"]