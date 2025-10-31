package com.zelda.codex.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Información de autenticación y autorización")
@SecurityRequirement(name = "oauth2")
public class AuthController {

    @GetMapping("/info")
    @Operation(summary = "Información del token", description = "Obtiene información sobre el token JWT actual y los permisos del usuario")
    public ResponseEntity<Map<String, Object>> getAuthInfo(Authentication authentication) {
        Map<String, Object> authInfo = new HashMap<>();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            
            // Información básica del token
            authInfo.put("authenticated", true);
            authInfo.put("principal", authentication.getName());
            authInfo.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
            
            // Claims del JWT
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", jwt.getSubject());
            claims.put("iss", jwt.getIssuer().toString());
            claims.put("aud", jwt.getAudience());
            claims.put("client_id", jwt.getClaim("client_id"));
            claims.put("scope", jwt.getClaim("scp"));
            
            // Información de tiempo
            if (jwt.getIssuedAt() != null) {
                claims.put("iat", LocalDateTime.ofInstant(jwt.getIssuedAt(), ZoneId.systemDefault()));
            }
            if (jwt.getExpiresAt() != null) {
                claims.put("exp", LocalDateTime.ofInstant(jwt.getExpiresAt(), ZoneId.systemDefault()));
                claims.put("expires_in_seconds", jwt.getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond());
            }
            
            authInfo.put("token_info", claims);
            
            // Permisos específicos de la API
            boolean canRead = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("SCOPE_read"));
            boolean canWrite = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("SCOPE_write"));
                
            Map<String, Boolean> permissions = new HashMap<>();
            permissions.put("read_weapons", canRead);
            permissions.put("write_weapons", canWrite);
            permissions.put("manage_cache", canWrite);
            
            authInfo.put("permissions", permissions);
            
        } else {
            authInfo.put("authenticated", false);
            authInfo.put("error", "No JWT token found");
        }
        
        authInfo.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(authInfo);
    }

    @GetMapping("/scopes")
    @Operation(summary = "Scopes disponibles", description = "Lista todos los scopes disponibles en la API")
    public ResponseEntity<Map<String, Object>> getAvailableScopes() {
        Map<String, Object> scopesInfo = new HashMap<>();
        
        Map<String, String> scopes = new HashMap<>();
        scopes.put("read", "Permite lectura de armas y consulta de información");
        scopes.put("write", "Permite crear, actualizar y eliminar armas");
        
        scopesInfo.put("available_scopes", scopes);
        
        Map<String, Object> endpoints = new HashMap<>();
        endpoints.put("read_endpoints", new String[]{
            "GET /api/v1/weapons",
            "GET /api/v1/weapons/{id}", 
            "GET /api/v1/cache/info"
        });
        endpoints.put("write_endpoints", new String[]{
            "POST /api/v1/weapons",
            "PUT /api/v1/weapons/{id}",
            "PATCH /api/v1/weapons/{id}",
            "DELETE /api/v1/weapons/{id}",
            "DELETE /cache/clear"
        });
        
        scopesInfo.put("protected_endpoints", endpoints);
        scopesInfo.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(scopesInfo);
    }
}