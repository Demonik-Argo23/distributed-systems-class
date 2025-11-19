namespace PokedexApi.Exceptions;

public class TrainerValidationException : Exception
{
    public TrainerValidationException(string message) : base(message)
    {
    }
}