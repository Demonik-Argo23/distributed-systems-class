using System.ComponentModel.DataAnnotations;

namespace CodexApi.Dtos;

public class UpdateWarframeRequest
{
    [Required(ErrorMessage = "Name is required")]
    [StringLength(100, ErrorMessage = "Name cannot exceed 100 characters")]
    public string Name { get; set; } = string.Empty;

    [Required(ErrorMessage = "Role is required")]
    [StringLength(50, ErrorMessage = "Role cannot exceed 50 characters")]
    public string Rol { get; set; } = string.Empty;

    [Range(0, 30, ErrorMessage = "Rank must be between 0 and 30")]
    public int Rank { get; set; }

    [Required]
    public StatsRequest Stats { get; set; } = new();
}