using PokedexApi.Models;

namespace PokedexApi.Gateways;
//Como si fuera un repositorio pero para un servicio externo
//Clean architecture - Interface Adapter / Hexagonal architecture
public interface IPokemonGateway
{
    Task<Pokemon> GetPokemonByIdAsync(Guid id, CancellationToken cancellationToken);
}