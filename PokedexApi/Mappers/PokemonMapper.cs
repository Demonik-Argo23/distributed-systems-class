using PokedexApi.Models;
using PokedexApi.Infrastructure.Soap.Dtos;
using PokedexApi.Dtos;


namespace PokedexApi.Mappers;

public static class PokemonMapper
{
    public static Pokemon ToModel(this PokemonResponseDto pokemonResponseDto)
    {
        return new Pokemon
        {
            Id = pokemonResponseDto.Id,
            Name = pokemonResponseDto.Name,
            Type = pokemonResponseDto.Type,
            Level = pokemonResponseDto.Level,
            Stats = new Stats
            {
                Attack = pokemonResponseDto.Stats.Attack,
                Defense = pokemonResponseDto.Stats.Defense,
                Speed = pokemonResponseDto.Stats.Speed,
                HP = pokemonResponseDto.Stats.Hp
            }
        };
    }
    public static PokemonResponse ToResponse(this Pokemon pokemon)
    {
        return new PokemonResponse
        {
            Id = pokemon.Id,
            Name = pokemon.Name,
            Attack = pokemon.Stats.Attack
        };
    }
}