package com.zelda.codex.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zelda.codex.dtos.CreateWeaponRequest;
import com.zelda.codex.dtos.ReplaceWeaponRequest;
import com.zelda.codex.dtos.UpdateWeaponRequest;
import com.zelda.codex.dtos.WeaponResponse;
import com.zelda.codex.exceptions.WeaponNotFoundException;
import com.zelda.codex.mappers.WeaponMapper;
import com.zelda.codex.models.Weapon;
import com.zelda.codex.services.HateoasLinkService;
import com.zelda.codex.services.IWeaponService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/weapons")
@Tag(name = "Weapons", description = "API del Codex de Armas de Zelda Breath of the Wild")
public class WeaponsController {

    private final IWeaponService weaponService;
    private final WeaponMapper weaponMapper;
    private final HateoasLinkService hateoasLinkService;

    @Autowired
    public WeaponsController(IWeaponService weaponService, WeaponMapper weaponMapper, HateoasLinkService hateoasLinkService) {
        this.weaponService = weaponService;
        this.weaponMapper = weaponMapper;
        this.hateoasLinkService = hateoasLinkService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener arma por ID", description = "Obtiene un arma específica del Codex por su ID")
    @SecurityRequirement(name = "oauth2", scopes = {"read"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Arma encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Arma no encontrada"),
        @ApiResponse(responseCode = "400", description = "ID de arma inválido")
    })
    public ResponseEntity<WeaponResponse> getWeaponById(
            @Parameter(description = "UUID del arma", required = true)
            @PathVariable UUID id) {
        
        Weapon weapon = weaponService.getWeaponById(id);
        WeaponResponse response = weaponMapper.toResponse(weapon);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Obtener listado de armas", description = "Obtiene un listado paginado de armas con filtros opcionales")
    @SecurityRequirement(name = "oauth2", scopes = {"read"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    public ResponseEntity<Page<WeaponResponse>> getAllWeapons(
            @Parameter(description = "Número de página (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int pageSize,
            
            @Parameter(description = "Campo de ordenamiento", example = "name")
            @RequestParam(defaultValue = "name") String sort,
            
            @Parameter(description = "Dirección de ordenamiento", example = "asc")
            @RequestParam(defaultValue = "asc") String direction,
            
            @Parameter(description = "Filtros adicionales")
            @RequestParam Map<String, String> filters) {

        filters.remove("page");
        filters.remove("pageSize");
        filters.remove("sort");
        filters.remove("direction");

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortDirection, sort));
        
        Page<Weapon> weaponPage = weaponService.getAllWeapons(pageable, filters);
        
        Page<WeaponResponse> responsePage = weaponPage.map(weaponMapper::toResponse);
        
        return ResponseEntity.ok(responsePage);
    }

    @PostMapping
    @Operation(summary = "Crear nueva arma", description = "Registra una nueva arma en el Codex")
    @SecurityRequirement(name = "oauth2", scopes = {"write"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Arma creada exitosamente con header Location"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos - error de validación"),
        @ApiResponse(responseCode = "409", description = "Conflicto - el arma ya existe")
    })
    public ResponseEntity<WeaponResponse> createWeapon(
            @Parameter(description = "Datos del arma a crear", required = true)
            @Valid @RequestBody CreateWeaponRequest request) {
        
        Weapon weapon = weaponMapper.toModel(request);
        Weapon createdWeapon = weaponService.createWeapon(weapon);
        WeaponResponse response = weaponMapper.toResponse(createdWeapon);
        
        String locationUri = hateoasLinkService.generateLocationHeader(createdWeapon.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.LOCATION, locationUri)
            .body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Reemplazar arma", description = "Reemplaza completamente un arma existente")
    @SecurityRequirement(name = "oauth2", scopes = {"write"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Arma reemplazada exitosamente"),
        @ApiResponse(responseCode = "201", description = "Arma creada porque no existía previamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos - error de validación"),
        @ApiResponse(responseCode = "404", description = "Arma no encontrada")
    })
    public ResponseEntity<WeaponResponse> replaceWeapon(
            @Parameter(description = "UUID del arma", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Datos completos del arma", required = true)
            @Valid @RequestBody ReplaceWeaponRequest request) {
        
        Weapon weapon = weaponMapper.toModel(request);
        
        boolean weaponExists = true;
        try {
            weaponService.getWeaponById(id);
        } catch (WeaponNotFoundException e) {
            weaponExists = false;
        }
        
        Weapon replacedWeapon = weaponService.replaceWeapon(id, weapon);
        WeaponResponse response = weaponMapper.toResponse(replacedWeapon);
        
        HttpStatus status = weaponExists ? HttpStatus.OK : HttpStatus.CREATED;
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(status);
        
        if (!weaponExists) {
            String locationUri = hateoasLinkService.generateLocationHeader(id);
            responseBuilder.header(HttpHeaders.LOCATION, locationUri);
        }
        
        return responseBuilder.body(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar arma parcialmente", description = "Actualiza parcialmente los campos especificados de un arma")
    @SecurityRequirement(name = "oauth2", scopes = {"write"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Arma actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Arma no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<WeaponResponse> updateWeapon(
            @Parameter(description = "UUID del arma", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Campos a actualizar (solo los campos especificados serán modificados)", required = true)
            @Valid @RequestBody UpdateWeaponRequest request) {
        
        Map<String, Object> updates = weaponMapper.toUpdateMap(request);
        Weapon updatedWeapon = weaponService.updateWeapon(id, updates);
        WeaponResponse response = weaponMapper.toResponse(updatedWeapon);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar arma", description = "Elimina un arma del Codex")
    @SecurityRequirement(name = "oauth2", scopes = {"write"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Arma eliminada exitosamente - sin contenido"),
        @ApiResponse(responseCode = "404", description = "Arma no encontrada"),
        @ApiResponse(responseCode = "400", description = "ID de arma inválido")
    })
    public ResponseEntity<Void> deleteWeapon(
            @Parameter(description = "UUID del arma", required = true)
            @PathVariable UUID id) {
        
        weaponService.deleteWeapon(id);
        return ResponseEntity.noContent().build();
    }
}