FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build-env
WORKDIR /app/server

# Copy csproj and restore as distinct layers
COPY auth/*.csproj ./app/server
RUN dotnet restore

# Copy .config and restore the dotnet tools
COPY auth/.config ./app/server/.config
RUN dotnet tool restore

# Copy everything else and build
COPY certs/ /app/certs
COPY auth/ /app/server
COPY auth/appsettings.Production.json ./app/server/appsettings.json
RUN dotnet publish -c Release -o out
RUN dotnet ef database update

# Build runtime image
FROM mcr.microsoft.com/dotnet/aspnet:6.0
WORKDIR /app
COPY --from=build-env /app/server/out .
ENTRYPOINT ["dotnet", "aspnetapp.dll"]