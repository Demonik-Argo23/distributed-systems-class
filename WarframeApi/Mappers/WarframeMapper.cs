using WarframeApi.Dtos;
using WarframeApi.Models;
using WarframeApi.Infrastructure.Entities;

namespace WarframeApi.Mappers;

public static class WarframeMapper
{
    public static IList<WarframeResponseDto> ToResponseDto(this IReadOnlyList<Warframe> warframes)
    {
        return warframes.Select(s => s.ToResponseDto()).ToList();
    }

    public static IReadOnlyList<Warframe> ToModel(this IReadOnlyList<WarframeEntity> warframes)
    {
        return warframes.Select(s => s.ToModel()).ToList();
    }

    public static Warframe ToModel(this WarframeEntity warframeEntity)
    {
        if (warframeEntity is null)
        {
            throw new ArgumentNullException(nameof(warframeEntity), "WarframeEntity cannot be null");
        }

        return new Warframe
        {
            Id = warframeEntity.Id,
            Name = warframeEntity.Name ?? string.Empty,
            Rol = warframeEntity.Rol ?? string.Empty,
            Rank = warframeEntity.Rank,
            Stats = new Stats
            {
                Hp = warframeEntity.Hp,
                Shield = warframeEntity.Shield,
                Armor = warframeEntity.Armor,
                Energy = warframeEntity.Energy
            }
        };
    }

    public static WarframeEntity ToEntity(this Warframe warframe)
    {
        if (warframe is null)
        {
            throw new ArgumentNullException(nameof(warframe), "Warframe cannot be null");
        }

        if (warframe.Stats is null)
        {
            throw new ArgumentException("Warframe Stats cannot be null", nameof(warframe));
        }

        return new WarframeEntity
        {
            Id = warframe.Id,
            Name = warframe.Name ?? string.Empty,
            Rol = warframe.Rol ?? string.Empty,
            Rank = warframe.Rank,
            Hp = warframe.Stats.Hp,
            Shield = warframe.Stats.Shield,
            Armor = warframe.Stats.Armor,
            Energy = warframe.Stats.Energy
        };
    }

    public static Warframe ToModel(this CreateWarframeDto createWarframeDto)
    {
        if (createWarframeDto is null)
        {
            throw new ArgumentNullException(nameof(createWarframeDto), "CreateWarframeDto cannot be null");
        }

        if (createWarframeDto.Stats is null)
        {
            throw new ArgumentException("CreateWarframeDto Stats cannot be null", nameof(createWarframeDto));
        }

        return new Warframe
        {
            Id = Guid.NewGuid(),
            Name = createWarframeDto.Name ?? string.Empty,
            Rol = createWarframeDto.Rol ?? string.Empty,
            Rank = createWarframeDto.Rank,
            Stats = new Stats
            {
                Hp = createWarframeDto.Stats.Hp,
                Shield = createWarframeDto.Stats.Shield,
                Armor = createWarframeDto.Stats.Armor,
                Energy = createWarframeDto.Stats.Energy
            }
        };
    }

    public static WarframeResponseDto ToResponseDto(this Warframe warframe)
    {
        if (warframe is null)
        {
            throw new ArgumentNullException(nameof(warframe), "Warframe cannot be null");
        }

        if (warframe.Stats is null)
        {
            throw new ArgumentException("Warframe Stats cannot be null", nameof(warframe));
        }

        return new WarframeResponseDto
        {
            Id = warframe.Id,
            Name = warframe.Name ?? string.Empty,
            Rol = warframe.Rol ?? string.Empty,
            Rank = warframe.Rank,
            Hp = warframe.Stats.Hp,
            Shield = warframe.Stats.Shield,
            Armor = warframe.Stats.Armor,
            Energy = warframe.Stats.Energy
        };
    }
}