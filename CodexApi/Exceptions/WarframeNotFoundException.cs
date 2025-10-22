namespace CodexApi.Exceptions;

public class WarframeNotFoundException : Exception
{
    public WarframeNotFoundException(Guid id) 
        : base($"Warframe with ID '{id}' was not found.")
    {
    }
}