using System.ServiceModel;
using WarframeApi.Dtos;

namespace WarframeApi.Validators;

public static class WarframeValidator
{
    public static CreateWarframeDto ValidateRequiredFields(this CreateWarframeDto warframe)
    {
        if (string.IsNullOrEmpty(warframe.Name))
            throw new FaultException("Warframe name is required and cannot be empty");
            
        if (string.IsNullOrEmpty(warframe.Rol))
            throw new FaultException("Warframe Rol is required and cannot be empty");
            
        if (warframe.Stats == null)
            throw new FaultException("Warframe Stats are required");
            
        return warframe;
    }

    public static CreateWarframeDto ValidateName(this CreateWarframeDto warframe) =>
        string.IsNullOrEmpty(warframe.Name)
            ? throw new FaultException("Warframe name is required")
            : warframe;

    public static CreateWarframeDto ValidateRol(this CreateWarframeDto warframe) =>
        string.IsNullOrEmpty(warframe.Rol)
            ? throw new FaultException("Warframe Rol is required")
            : warframe;
        
    public static CreateWarframeDto ValidateRank(this CreateWarframeDto warframe)
    {
        if (warframe.Rank < 0 || warframe.Rank > 30)
        {
            throw new FaultException("Warframe Rank must be between 0 and 30 (inclusive)");
        }
        
        return warframe;
    }

    public static CreateWarframeDto ValidateStats(this CreateWarframeDto warframe) =>
        warframe.Stats == null
            ? throw new FaultException("Warframe Stats are required")
            : warframe;
}