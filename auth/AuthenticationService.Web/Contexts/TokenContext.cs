using System.Runtime.InteropServices;
using AuthenticationService.Domain.Token;
using Microsoft.EntityFrameworkCore;

public class TokenContext : DbContext
{
    class TokenEntity {
        public Guid Id { get; set; } = Guid.NewGuid();
        public Guid OwnerId { get; set; }
        public DateTime IssuedAt { get; set; }
        public TimeSpan ExpiresIn { get; set; }
        public string Value { get; set; } = string.Empty;
        public byte[] Hash { get; set; } = Array.Empty<byte>();
    }
    private static Func<TokenContext, byte[], Task<TokenEntity>> FindTokenByHash = 
        EF.CompileAsyncQuery((TokenContext ctx, byte[] hash) => 
            ctx.Set<TokenEntity>().Where(entity => entity.Hash == hash).First());
    public TokenContext(DbContextOptions<TokenContext> options) : base(options) {}

    public async Task<Token> FindTokenByHashAsync(byte[] hash) {
        var token = await FindTokenByHash(this, hash);

        return new Token(token.Value, token.OwnerId, token.IssuedAt, token.ExpiresIn);
    }

    public Task AddTokenAsync(in Token token) {
        if (!MemoryMarshal.TryGetArray(token.Hash.Value, out var segment) || segment.Array is null)
            throw new MemoryAccessException("Failure trying to access array");

        TokenEntity entity = new() {
            Id = token.Id,
            OwnerId = token.OwnerId,
            IssuedAt = token.IssuedAt.Value,
            ExpiresIn = token.ExpiresIn.Value,
            Hash = segment.Array
        };

        return AddTokenAsync(entity);
    }

    private async Task AddTokenAsync(TokenEntity token) {
        await Set<TokenEntity>().AddAsync(token);
        await SaveChangesAsync();
    }
}