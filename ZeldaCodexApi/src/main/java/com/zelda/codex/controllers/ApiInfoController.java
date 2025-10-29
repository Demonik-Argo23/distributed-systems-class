package com.zelda.codex.controllers;

import com.zelda.codex.dtos.Link;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "API Info", description = "Información general de la API")
public class ApiInfoController {

    @GetMapping
    @Operation(summary = "Información de la API", description = "Obtiene información general sobre los endpoints disponibles con enlaces HATEOAS")
    @ApiResponse(responseCode = "200", description = "Información obtenida exitosamente")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> apiInfo = new HashMap<>();
        String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
        
        apiInfo.put("name", "Zelda Codex API");
        apiInfo.put("version", "1.0.0");
        apiInfo.put("description", "API REST Gateway para el Codex de armas de Zelda Breath of the Wild");
        apiInfo.put("timestamp", LocalDateTime.now());
        
        // Enlaces HATEOAS a recursos principales
        List<Link> links = new ArrayList<>();
        links.add(new Link("self", baseUri + "/api/v1", "GET"));
        links.add(new Link("weapons", baseUri + "/api/v1/weapons", "GET"));
        links.add(new Link("create-weapon", baseUri + "/api/v1/weapons", "POST"));
        links.add(new Link("swagger-ui", baseUri + "/swagger-ui.html", "GET"));
        links.add(new Link("api-docs", baseUri + "/api-docs", "GET"));
        
        apiInfo.put("_links", links);
        
        Map<String, Object> endpoints = new HashMap<>();
        endpoints.put("weapons", Map.of(
            "GET /api/v1/weapons/{id}", "Obtener arma por ID",
            "GET /api/v1/weapons", "Obtener listado paginado de armas con filtros",
            "POST /api/v1/weapons", "Crear nueva arma", 
            "PUT /api/v1/weapons/{id}", "Reemplazar arma completamente",
            "PATCH /api/v1/weapons/{id}", "Actualizar arma parcialmente",
            "DELETE /api/v1/weapons/{id}", "Eliminar arma"
        ));
        
        apiInfo.put("endpoints", endpoints);
        
        Map<String, String> documentation = new HashMap<>();
        documentation.put("swagger-ui", baseUri + "/swagger-ui.html");
        documentation.put("api-docs", baseUri + "/api-docs");
        
        apiInfo.put("documentation", documentation);
        
        return ResponseEntity.ok(apiInfo);
    }
}