using AuthenticationService.Application.Interfaces;
using AuthenticationService.Application.Models;
using AuthenticationService.Domain.Token;
using AuthenticationService.Domain.User;
using AuthenticationService.Web.Contexts.Comparers;
using AuthenticationService.Web.Contexts.Converters;
using AuthenticationService.Web.Contexts.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using AuthenticationService.Web.Extensions;

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
    IUserRetrievalBuilder IUserRepository.FindAsync(Guid id)
    {
        ValueFactory = async (withToken) =>
        {
            var userQuery = Users
                    .Where(entity => entity.Id == id && !entity.Deleted)
                    .AsNoTracking();
            var entity = await (withToken ? userQuery.Include(user => user.Token) : userQuery).FirstOrDefaultAsync();

            if (entity is null)
#warning TODO: Implement user not found exception
                throw new Exception();

            return entity;
        };

        return this;
        }
    IUserRetrievalBuilder IUserRepository.FindAsync(UserCredentials credentials)
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

        var entity = await (withToken ? userQuery.Include(user => user.Token) : userQuery).FirstOrDefaultAsync();

        if (entity is null)
            throw new InvalidUsernameException(username);

        if (!password.Span.SequenceEqual(entity.Password))
            throw new InvalidPasswordException(password);

        return entity;
    }
    Task IUserRepository.SaveAsync(User user)
    {
        Users.Update(user.ToEntity());

        return SaveChangesAsync();
    }
    Task IUserRepository.CreateAsync(User user)
    {
        return StoreAsync(user.ToEntity());
    }
    async Task IUserRepository.DeleteAsync(Guid id)
    {
        var entity = await Users
                    .Where(entity => entity.Id == id && !entity.Deleted)
                    .FirstOrDefaultAsync();

        if (entity is null) return;

        entity.Deleted = true;

        await SaveChangesAsync();
    }
    async Task IUserRepository.UndoDeleteAsync(Guid id)
    {
        var entity = await Users.Where(entity => entity.Id == id && entity.Deleted).FirstOrDefaultAsync();

        if (entity is null) return;

        entity.Deleted = false;

        await SaveChangesAsync();
    }
    private async Task StoreAsync(UserEntity entity)
    {
        await Users.AddAsync(entity);

        await SaveChangesAsync();
    }
    #endregion
    #region IUserRetrievalBuilder
    Task<User> IUserRetrievalBuilder.Value => ValueFactory(false).ContinueWith(userTask => userTask.Result.ToUser());
    async Task<UserWithToken> IUserRetrievalBuilder.WithToken()
    {
        var entity = await ValueFactory(true);

        return entity.ToUserWithToken();
    }
    #endregion

}
