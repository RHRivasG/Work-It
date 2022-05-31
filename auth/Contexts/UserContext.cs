using Microsoft.EntityFrameworkCore;

namespace AuthenticationService.Contexts;

public class UserContext : DbContext {
    public DbSet<User> Users { get; set; }
    public UserContext(DbContextOptions<UserContext> options) : base(options) {}
    protected override void OnModelCreating(ModelBuilder modelBuilder) {
        var entity = modelBuilder.Entity<User>();
        entity.ToTable("work-it-users");
        entity
            .Property(user => user.Preferences)
            .HasConversion(
                preferences => string.Join(',', preferences),
                rawPreferences => rawPreferences.Split(',', StringSplitOptions.RemoveEmptyEntries)
            );
        entity
            .HasKey(user => new { user.Id });
    }
}

public class User {
    public Guid Id { get; set; }
    public string Name { get; set; }
    public string Password { get; set; }
    public string Role { get; set; }
    public string[] Preferences { get; set; } 
    public string[] AvailableRoles {
        get => Role switch
        {
            "trainer" => new[] { "trainer", "participant" }, 
            "participant" => new[] { "participant" },
            "admin" => new[] { "admin" },
            _ => new string[] {}
        };
    }
}