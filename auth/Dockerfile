# Compile application
FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build-env
WORKDIR /app
    
# Set up dependencies
COPY certs/ /certs
COPY protobuf/ /protobuf

# Restore project
COPY auth/.config ./
COPY auth/AuthenticationService.Web/*.csproj ./AuthenticationService.Web/
COPY auth/AuthenticationService.Domain/*.csproj ./AuthenticationService.Domain/
COPY auth/AuthenticationService.Application/*.csproj ./AuthenticationService.Application/
COPY auth/auth.sln ./auth.sln

RUN dotnet tool restore
RUN dotnet restore

# Copy projects files
COPY auth/AuthenticationService.Web/appsettings.Docker.json ./AuthenticationService.Web/appsettings.Production.json
COPY auth/AuthenticationService.Web/ ./AuthenticationService.Web
COPY auth/AuthenticationService.Domain/ ./AuthenticationService.Domain
COPY auth/AuthenticationService.Application/ ./AuthenticationService.Application

# Publish project

WORKDIR /app/AuthenticationService.Web

RUN dotnet build
RUN dotnet ef database update
RUN dotnet publish -c Release -o out
    
# Run application
FROM mcr.microsoft.com/dotnet/aspnet:6.0
WORKDIR /app
COPY --from=build-env /app/AuthenticationService.Web/out .
COPY --from=build-env /app/AuthenticationService.Web/work-it.db .
COPY --from=build-env /certs /certs
ENTRYPOINT ["dotnet", "AuthenticationService.Web.dll"]
