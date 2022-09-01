using AuthenticationService.Application.Interfaces;
using AuthenticationService.Application.Models;
using AuthenticationService.Domain.Token;
using AuthenticationService.Domain.User;
using AuthenticationService.Web.Contexts.Comparers;
using AuthenticationService.Web.Contexts.Converters;
using AuthenticationService.Web.Contexts.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;

namespace AuthenticationService.Web.Contexts;

public partial class UserContext : DbContext, IUserRepository, IUserRetrievalBuilder
{
    #region DbSet part
    public DbSet<UserEntity> Users => Set<UserEntity>();
    public DbSet<TokenEntity> Tokens => Set<TokenEntity>();
    private Func<bool, Task<UserEntity>> ValueFactory { get; set; }
    public UserContext(DbContextOptions options) : base(options)
    {
        ValueFactory = _ => Task.FromException<UserEntity>(new Exception());
    }
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        var userEntity = modelBuilder.Entity<UserEntity>();
        var tokenEnitty = modelBuilder.Entity<TokenEntity>();

        userEntity
            .Property(user => user.Preferences)
            .HasConversion<StringArrayToStringValueConverter, StringArrayValueComparer>();

        userEntity
            .Property(user => user.Role)
            .HasConversion<StringArrayToStringValueConverter, StringArrayValueComparer>();

        userEntity
            .HasKey(user => new { user.Id });

        userEntity
            .Property(user => user.Id)
            .HasConversion<GuidToStringConverter>();

        tokenEnitty
            .Property(token => token.Id)
            .HasConversion<GuidToStringConverter>();

        tokenEnitty
            .Property(token => token.OwnerId)
            .HasConversion<GuidToStringConverter>();

        tokenEnitty
            .Property(token => token.Hash)
            .HasConversion<string>();

        tokenEnitty.HasIndex(token => token.Hash);
    }
    #endregion
    #region IUserRepository part
    public IUserRetrievalBuilder FindAsync(Guid id)
    {
        ValueFactory = async (withToken) =>
        {
            var entity = withToken ?
                await Users
                    .Where(entity => entity.Id == id && !entity.Deleted)
                    .AsNoTracking()
                    .Include(user => user.Token)
                    .FirstOrDefaultAsync() :
                await Users
                    .Where(entity => entity.Id == id && !entity.Deleted)
                    .AsNoTracking()
                    .FirstOrDefaultAsync();

            if (entity is null)
#warning TODO: Implement user not found exception
                throw new Exception();

            return entity;
        };

        return this;
    }
    public IUserRetrievalBuilder FindAsync(in UserCredentials credentials)
    {
        var username = credentials.Username;
        var password = credentials.Password;
        var role = credentials.Role;

        ValueFactory = (withToken) => FindAsync(username, password, role.ToArray(), withToken);

        return this;
    }
    private async Task<UserEntity> FindAsync(ReadOnlyMemory<char> username, ReadOnlyMemory<char> password, string[] roles, bool withToken)
    {
        var userQuery = Users
                .Where(entity => entity.Name == username.ToString() && entity.Password == password.ToString() && entity.Role == roles && !entity.Deleted)
                .AsNoTracking();

        var entity = (withToken ? userQuery.Include(user => user.Token) : userQuery).FirstOrDefault();

        if (entity is null)
            throw new InvalidUsernameException(username);

        if (!password.Span.SequenceEqual(entity.Password))
            throw new InvalidPasswordException(password);

        return entity;
    }
    public async Task SaveAsync(User user)
    {
        var entity = new UserEntity
        {
            Name = user.Username.ToString(),
            Password = user.Password.ToString(),
            Preferences = user.Preferences,
            Role = user.Role.ToArray(),
            Id = user.Id
        };

        Users.Update(entity);

        await SaveChangesAsync();
    }
    public async Task CreateAsync(User user)
    {
        var entity = new UserEntity
        {
            Name = user.Username.ToString(),
            Password = user.Password.ToString(),
            Preferences = user.Preferences,
            Role = user.Role.ToArray(),
            Id = user.Id
        };

        await Users.AddAsync(entity);

        await SaveChangesAsync();
    }
    public async Task DeleteAsync(Guid id)
    {
        var entity = await Users
                    .Where(entity => entity.Id == id && !entity.Deleted)
                    .FirstOrDefaultAsync();

        if (entity is null) return;

        entity.Deleted = true;

        await SaveChangesAsync();
    }
    public async Task UndoDeleteAsync(Guid id)
    {
        var entity = await Users
                    .Where(entity => entity.Id == id && entity.Deleted)
                    .FirstOrDefaultAsync();

        if (entity is null) return;

        entity.Deleted = false;

        await SaveChangesAsync();
    }
    #endregion
    #region IUserRetrievalBuilder
    public Task<User> Value => ValueFactory(false).ContinueWith(userTask =>
    {
        var entity = userTask.Result;
        return new User(entity.Role.ToRole(), entity.Name.AsMemory(), entity.Password.AsMemory(), entity.Preferences, entity.Id);
    });
    public async Task<UserWithToken> WithToken()
    {
        var entity = await ValueFactory(true);
        var token = entity.Token;

        return new UserWithToken
        {
            User = new User(entity.Role.ToRole(), entity.Name.AsMemory(), entity.Password.AsMemory(), entity.Preferences, entity.Id),
            Token = token is null ? null : new Token(token.Value, token.OwnerId, token.IssuedAt, token.ExpiresIn)
        };
    }
    #endregion

}
