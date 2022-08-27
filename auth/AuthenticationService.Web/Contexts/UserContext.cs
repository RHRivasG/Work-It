using System.Runtime.InteropServices;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Application.Models;
using AuthenticationService.Domain.User;
using Microsoft.EntityFrameworkCore;

namespace AuthenticationService.Web.Contexts;

public class UserContext : DbContext, IUserRepository {
    class UserEntity
    {
        public Guid Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
        public string[] Role { get; set; } = Array.Empty<string>();
        public string[] Preferences { get; set; } = Array.Empty<string>();
        public bool Deleted { get; set; } = false;
    }
    private static Func<UserContext, string, string, string[], Task<UserEntity>> FindUserByCredentials =
        EF.CompileAsyncQuery(
            (UserContext context, string username, string password, string[] roles) => 
                context
                    .Set<UserEntity>()
                    .Where(entity => entity.Name == username && entity.Role == roles && !entity.Deleted)
                    .AsNoTracking()
                    .First()
        );
    private static Func<UserContext, Guid, Task<UserEntity>> FindNonDeletedUser =
        EF.CompileAsyncQuery(
            (UserContext context, Guid id) => 
                context
                    .Set<UserEntity>()
                    .Where(entity => entity.Id == id && !entity.Deleted)
                    .First()
        );
    private static Func<UserContext, Guid, Task<UserEntity>> FindDeletedUser =
        EF.CompileAsyncQuery(
            (UserContext context, Guid id) => 
                context
                    .Set<UserEntity>()
                    .Where(entity => entity.Id == id && entity.Deleted)
                    .First()
        );
    public UserContext(DbContextOptions options) : base(options) {}
    protected override void OnModelCreating(ModelBuilder modelBuilder) {
        var entity = modelBuilder.Entity<UserEntity>();

        entity.ToTable("work-it-users");

        entity
            .Property(user => user.Preferences)
            .HasConversion(
                preferences => string.Join(',', preferences),
                rawPreferences => rawPreferences.Split(',', StringSplitOptions.RemoveEmptyEntries)
            );

        entity
            .Property(user => user.Role)
            .HasConversion(
                preferences => string.Join(',', preferences),
                rawPreferences => rawPreferences.Split(',', StringSplitOptions.RemoveEmptyEntries)
            );

        entity
            .HasKey(user => new { user.Id });
    }
    public async Task<User> FindAsync(Guid id)
    {
        var entity = await FindNonDeletedUser(this, id);
        
        return new User(entity.Role.ToRole(), entity.Name, entity.Password, entity.Preferences, entity.Id);
    }
    public Task<User> FindAsync(in UserCredentials credentials)
    {
        var username = credentials.GetUsername();
        var password = credentials.GetPassword();
        var role = credentials.Role;

        return FindAsync(username, password, role.ToArray());
    }
    private async Task<User> FindAsync(string username, string password, string[] roles) {
        var entity = await FindUserByCredentials(this, username, password, roles);

        if (entity is null)
            throw new InvalidUsernameException(username);

        if (entity.Password != password)
            throw new InvalidPasswordException(password);

        return new User(entity.Role.ToRole(), entity.Name, entity.Password, entity.Preferences, entity.Id);
    }
    public async Task SaveAsync(User user)
    {
        var entity = new UserEntity {
            Name = user.Username,
            Password = user.Password,
            Preferences = user.Preferences,
            Role = user.Role.ToArray(),
            Id = user.Id
        };

        var entry = Entry(entity);
        entry.Property(ent => ent.Name).CurrentValue = user.Username;
        entry.Property(ent => ent.Password).CurrentValue = user.Password;
        entry.Property(ent => ent.Preferences).CurrentValue = user.Preferences;
        entry.Property(ent => ent.Role).CurrentValue = user.Role.ToArray();

        await SaveChangesAsync();
    }
    public async Task CreateAsync(User user) {
        var entity = new UserEntity {
            Name = user.Username,
            Password = user.Password,
            Preferences = user.Preferences,
            Role = user.Role.ToArray(),
            Id = user.Id
        };

        await Set<UserEntity>().AddAsync(entity);

        await SaveChangesAsync();
    }
    public async Task DeleteAsync(Guid id)
    {
        var entity = await FindNonDeletedUser(this, id);

        if (entity is null) return;

        entity.Deleted = true;

        await SaveChangesAsync();
    }
    public async Task UndoDeleteAsync(Guid id)
    {
        var entity = await FindDeletedUser(this, id);

        if (entity is null) return;

        entity.Deleted = false;

        await SaveChangesAsync();
    }
}