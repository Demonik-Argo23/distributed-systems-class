using Microsoft.EntityFrameworkCore;
using PokemonApi.Infrastructure;
using PokemonApi.Models;
using PokemonApi.Mappers;

namespace PokemonApi.Repositories;

public class PokemonRepository : IPokemonRepository
{
    private readonly RelationalDbContext _context;

    public PokemonRepository(RelationalDbContext context)
    {
        _context = context;

    }

    public async Task UpdatePokemonAsync(Pokemon pokemon, CancellationToken cancellationToken)
    {
        //UPDATE Pokemons SET Name = 'name', Type = 'type', Attack = 10, Defense = 10, Speed = 10, Hp = 10 WHERE Id = 'id';
        _context.Pokemons.Update(pokemon.ToEntity());
        await _context.SaveChangesAsync(cancellationToken);
    }

    public async Task DeletePokemonAsync(Pokemon pokemon, CancellationToken cancellationToken)
    {
        //DELETE * FROM Pokemons WHERE Id = 'id';
        _context.Pokemons.Remove(pokemon.ToEntity());
        await _context.SaveChangesAsync(cancellationToken);
    }

    public async Task<IReadOnlyList<Pokemon>> GetPokemonsByNameAsync(string name, CancellationToken cancellationToken)
    {
        //select * from pokemons where name like '%TEXTO%' 
        var pokemons = await _context.Pokemons.AsNoTracking()
        .Where(s => s.Name.Contains(name)).ToListAsync(cancellationToken);
        return pokemons.ToModel();
    }

    public async Task<Pokemon?> GetPokemonByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        //SELECT * FROM Pokemons WHERE Id = id LIMIT 1
        var pokemon = await _context.Pokemons.AsNoTracking().FirstOrDefaultAsync(s => s.Id == id, cancellationToken);
        return pokemon?.ToModel();
    }

    public async Task<Pokemon> GetByNameAsync(string name, CancellationToken cancellationToken)
    {
        //select * from pokemons where name like '%TEXTO%' LIMIT 1
        var pokemon = await _context.Pokemons.AsNoTracking().FirstOrDefaultAsync(s => s.Name.Contains(name));
        return pokemon.ToModel();
    }

    public async Task<Pokemon> CreateAsync(Pokemon pokemon, CancellationToken cancellationToken)
    {
        var pokemonToCreate = pokemon.ToEntity();
        pokemonToCreate.Id = Guid.NewGuid();
        await _context.Pokemons.AddAsync(pokemonToCreate, cancellationToken);
        await _context.SaveChangesAsync(cancellationToken);

        return pokemonToCreate.ToModel();
    }

    public async Task<(IReadOnlyList<Pokemon> data, int totalRecords)> GetPokemonsAsync(string name, string type, int pageNumber, int pageSize, string orderBy, string orderDirection, CancellationToken cancellationToken)
    {
        var query = _context.Pokemons.AsNoTracking();

        if (!string.IsNullOrEmpty(name))
        {
            query = query.Where(s => s.Name.Contains(name));
        }

        if (!string.IsNullOrEmpty(type))
        {
            query = query.Where(s => s.Type != null && s.Type.ToLower().Contains(type.ToLower()));
        }

        var totalRecords = await query.CountAsync(cancellationToken);

        var isDescending = orderDirection.Equals("desc", StringComparison.OrdinalIgnoreCase);
        query = orderBy.ToLower() switch
        {
            "name" => isDescending ? query.OrderByDescending(p => p.Name) : query.OrderBy(p => p.Name),
            "type" => isDescending ? query.OrderByDescending(p => p.Type) : query.OrderBy(p => p.Type),
            "attack" => isDescending ? query.OrderByDescending(p => p.Attack) : query.OrderBy(p => p.Attack), // Usa la propiedad de la entidad!
            _ => query.OrderBy(p => p.Name),
        };

        var pokemons = await query
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .ToListAsync(cancellationToken);

        return (pokemons.ToModel(), totalRecords);
    }
}