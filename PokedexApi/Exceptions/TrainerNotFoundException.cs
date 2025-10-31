namespace PokedexApi.Exceptions;

public class TrainerNotFoundException : Exception
{
    public TrainerNotFoundException(string id)
        : base($"Trainer with ID {id} was not found.")
    {
    }
}