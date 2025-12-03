namespace PokedexApi.Exceptions;

public class TrainerAlreadyExistsException : Exception
{
    public TrainerAlreadyExistsException(string id) : base("Trainer with id " + id + " already exists.")
    {
        
    }

}