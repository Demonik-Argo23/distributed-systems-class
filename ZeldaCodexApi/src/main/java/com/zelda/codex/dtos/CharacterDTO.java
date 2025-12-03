package com.zelda.codex.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

/**
 * DTO for Character data transfer between REST API and clients.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Character from Zelda games")
public class CharacterDTO {

    @Schema(description = "Character ID (MongoDB ObjectId)", example = "507f1f77bcf86cd799439011")
    private String id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, message = "Name must be at least 2 characters")
    @Schema(description = "Character name", example = "Link", required = true)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Character email", example = "link@hyrule.com", required = true)
    private String email;

    @NotBlank(message = "Game is required")
    @Schema(description = "Game title", example = "Breath of the Wild", required = true)
    private String game;

    @Schema(description = "Character race", example = "Hylian")
    private String race;

    @Min(value = 1, message = "Health must be at least 1")
    @Max(value = 999, message = "Health cannot exceed 999")
    @Schema(description = "Health points", example = "150", required = true)
    private Integer health;

    @Min(value = 1, message = "Stamina must be at least 1")
    @Max(value = 999, message = "Stamina cannot exceed 999")
    @Schema(description = "Stamina points", example = "120", required = true)
    private Integer stamina;

    @Min(value = 1, message = "Attack must be at least 1")
    @Max(value = 999, message = "Attack cannot exceed 999")
    @Schema(description = "Attack power", example = "80", required = true)
    private Integer attack;

    @Min(value = 1, message = "Defense must be at least 1")
    @Max(value = 999, message = "Defense cannot exceed 999")
    @Schema(description = "Defense power", example = "70", required = true)
    private Integer defense;

    @Schema(description = "List of weapon IDs", example = "[\"1\", \"5\", \"10\"]")
    private List<String> weapons;

    @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00Z")
    private String createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-15T10:30:00Z")
    private String updatedAt;

    // Constructors
    public CharacterDTO() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public Integer getStamina() {
        return stamina;
    }

    public void setStamina(Integer stamina) {
        this.stamina = stamina;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public Integer getDefense() {
        return defense;
    }

    public void setDefense(Integer defense) {
        this.defense = defense;
    }

    public List<String> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<String> weapons) {
        this.weapons = weapons;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
