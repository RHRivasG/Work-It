using Microsoft.EntityFrameworkCore.ChangeTracking;

namespace AuthenticationService.Web.Contexts.Comparers;

public class StringArrayValueComparer : ValueComparer<string[]>
{
    public StringArrayValueComparer() : base(
        (p1, p2) => p1 == null || p2 == null ? false : p1.SequenceEqual(p2),
            p => p.Aggregate(0, (acc, c) => HashCode.Combine(c.GetHashCode(), acc)),
            p => p.ToArray()
    ){}
}