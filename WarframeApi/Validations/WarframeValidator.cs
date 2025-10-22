using System.ServiceModel;
using WarframeApi.Dtos;

namespace WarframeApi.Validators;

public static class WarframeValidator
{
    public static CreateWarframeDto ValidateRequiredFields(this CreateWarframeDto warframe)
    {
        if (warframe is null)
            throw new FaultException("Warframe data is required and cannot be null");
            
        if (string.IsNullOrWhiteSpace(warframe.Name))
            throw new FaultException("Warframe name is required and cannot be empty");
            
        if (string.IsNullOrWhiteSpace(warframe.Rol))
            throw new FaultException("Warframe Rol is required and cannot be empty");
            
        if (warframe.Stats == null)
            throw new FaultException("Warframe Stats are required");
            
        return warframe;
    }

    public static CreateWarframeDto ValidateName(this CreateWarframeDto warframe) =>
        string.IsNullOrWhiteSpace(warframe?.Name)
            ? throw new FaultException("Warframe name is required")
            : warframe;

    public static CreateWarframeDto ValidateRol(this CreateWarframeDto warframe) =>
        string.IsNullOrWhiteSpace(warframe?.Rol)
            ? throw new FaultException("Warframe Rol is required")
            : warframe;
        
    public static CreateWarframeDto ValidateRank(this CreateWarframeDto warframe)
    {
        if (warframe is null)
            throw new FaultException("Warframe data is required");
            
        if (warframe.Rank < 0 || warframe.Rank > 30)
        {
            throw new FaultException("Warframe Rank must be between 0 and 30 (inclusive)");
        }
        
        return warframe;
    }

    public static CreateWarframeDto ValidateStats(this CreateWarframeDto warframe)
    {
        if (warframe is null)
            throw new FaultException("Warframe data is required");
            
        if (warframe.Stats == null)
            throw new FaultException("Warframe Stats are required");
            
        // Validate individual stat values
        if (warframe.Stats.Hp < 0)
            throw new FaultException("Warframe HP must be a positive value");
            
        if (warframe.Stats.Shield < 0)
            throw new FaultException("Warframe Shield must be a positive value");
            
        if (warframe.Stats.Armor < 0)
            throw new FaultException("Warframe Armor must be a positive value");
            
        if (warframe.Stats.Energy < 0)
            throw new FaultException("Warframe Energy must be a positive value");
            
        return warframe;
    }
}