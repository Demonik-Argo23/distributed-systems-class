namespace CodexApi.Exceptions;

public class WarframeAlreadyExistsException : Exception
{
    public WarframeAlreadyExistsException(string name) 
        : base($"Warframe with name '{name}' already exists.")
    {
    }
}