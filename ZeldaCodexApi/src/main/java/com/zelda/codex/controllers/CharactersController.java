package com.zelda.codex.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zelda.codex.dtos.CharacterDTO;
import com.zelda.codex.gateways.CharacterGrpcGateway;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/characters")
@Tag(name = "Characters", description = "Character management API")
public class CharactersController {

    private static final Logger logger = LoggerFactory.getLogger(CharactersController.class);

    private final CharacterGrpcGateway characterGrpcGateway;

    public CharactersController(CharacterGrpcGateway characterGrpcGateway) {
        this.characterGrpcGateway = characterGrpcGateway;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get character by ID", description = "Retrieves a character via gRPC Unary call")
    public ResponseEntity<CharacterDTO> getCharacter(@PathVariable String id) {
        logger.info("REST: Getting character with id: {}", id);
        
        try {
            CharacterDTO character = characterGrpcGateway.getCharacterById(id);
            return ResponseEntity.ok(character);
        } catch (RuntimeException e) {
            logger.error("Error getting character: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_write')")
    @Operation(summary = "Create character", description = "Creates a character via gRPC Unary call")
    public ResponseEntity<CharacterDTO> createCharacter(@Valid @RequestBody CharacterDTO dto) {
        logger.info("REST: Creating character: {}", dto.getName());
        
        try {
            CharacterDTO created = characterGrpcGateway.createCharacter(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            logger.error("Error creating character: {}", e.getMessage());
            
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAuthority('SCOPE_write')")
    @Operation(summary = "Create characters in batch", description = "Creates multiple characters via gRPC Client Streaming")
    public ResponseEntity<Map<String, Object>> createCharactersBatch(@Valid @RequestBody List<CharacterDTO> characters) {
        logger.info("REST: Creating batch of {} characters", characters.size());
        
        try {
            CharacterGrpcGateway.BatchResult result = characterGrpcGateway.createCharactersBatch(characters);
            
            Map<String, Object> response = Map.of(
                    "created_count", result.createdCount,
                    "created_ids", result.createdIds,
                    "errors", result.errors
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            logger.error("Error creating characters batch: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/by-game/{game}")
    @Operation(summary = "List characters by game", description = "Lists characters filtered by game via gRPC Server Streaming")
    public ResponseEntity<List<CharacterDTO>> listCharactersByGame(
            @PathVariable String game,
            @RequestParam(defaultValue = "100") int limit) {
        logger.info("REST: Listing characters for game: {}", game);
        
        try {
            List<CharacterDTO> characters = characterGrpcGateway.listCharactersByGame(game, limit);
            return ResponseEntity.ok(characters);
        } catch (RuntimeException e) {
            logger.error("Error listing characters by game: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_write')")
    @Operation(summary = "Update character", description = "Updates a character via gRPC Unary call")
    public ResponseEntity<CharacterDTO> updateCharacter(
            @PathVariable String id,
            @RequestBody CharacterDTO dto) {
        logger.info("REST: Updating character with id: {}", id);
        
        try {
            CharacterDTO updated = characterGrpcGateway.updateCharacter(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            logger.error("Error updating character: {}", e.getMessage());
            
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_write')")
    @Operation(summary = "Delete character", description = "Deletes a character via gRPC Unary call")
    public ResponseEntity<Map<String, Object>> deleteCharacter(@PathVariable String id) {
        logger.info("REST: Deleting character with id: {}", id);
        
        try {
            boolean success = characterGrpcGateway.deleteCharacter(id);
            
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Character deleted"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "Failed to delete character"));
            }
        } catch (RuntimeException e) {
            logger.error("Error deleting character: {}", e.getMessage());
            
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Character not found"));
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/by-weapon/{weaponId}")
    @Operation(summary = "List characters by weapon", description = "Lists all characters that use a specific weapon via gRPC")
    public ResponseEntity<List<CharacterDTO>> listCharactersByWeapon(@PathVariable String weaponId) {
        logger.info("REST: Listing characters with weapon id: {}", weaponId);
        
        try {
            List<CharacterDTO> characters = characterGrpcGateway.listCharactersByWeapon(weaponId);
            return ResponseEntity.ok(characters);
        } catch (RuntimeException e) {
            logger.error("Error listing characters by weapon: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
