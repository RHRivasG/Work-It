using Microsoft.EntityFrameworkCore.Storage.ValueConversion;

namespace AuthenticationService.Web.Contexts.Converters;

public class StringArrayToStringValueConverter : ValueConverter<string[], string>
{
    public StringArrayToStringValueConverter() : base(
        preferences => string.Join(',', preferences),
        rawPreferences => rawPreferences.Split(',', StringSplitOptions.RemoveEmptyEntries)
    ) {}
}