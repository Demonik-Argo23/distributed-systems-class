using System.ComponentModel.DataAnnotations;

namespace CodexApi.Dtos;

public class PatchWarframeRequest
{
    [StringLength(100, ErrorMessage = "Name cannot exceed 100 characters")]
    public string? Name { get; set; }

    [StringLength(50, ErrorMessage = "Role cannot exceed 50 characters")]
    public string? Rol { get; set; }

    [Range(0, 30, ErrorMessage = "Rank must be between 0 and 30")]
    public int? Rank { get; set; }

    public PatchStatsRequest? Stats { get; set; }
}

public class PatchStatsRequest
{
    [Range(1, 9999, ErrorMessage = "HP must be between 1 and 9999")]
    public int? Hp { get; set; }

    [Range(0, 9999, ErrorMessage = "Shield must be between 0 and 9999")]
    public int? Shield { get; set; }

    [Range(0, 9999, ErrorMessage = "Armor must be between 0 and 9999")]
    public int? Armor { get; set; }

    [Range(1, 9999, ErrorMessage = "Energy must be between 1 and 9999")]
    public int? Energy { get; set; }
}