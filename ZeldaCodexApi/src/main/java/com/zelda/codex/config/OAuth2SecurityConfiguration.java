package com.zelda.codex.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class OAuth2SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso público a Swagger/OpenAPI y actuator
                .requestMatchers("/swagger-ui/*", "/v3/api-docs/*", "/swagger-ui.html", "/actuator/**", "/api/v1/cache/**").permitAll()
                // El resto requiere autenticación OAuth2
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        
        return http.build();
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        
        // Converter personalizado que debuggea los scopes
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            System.out.println("=== JWT DEBUGGING ===");
            System.out.println("JWT Header: " + jwt.getHeaders());
            System.out.println("JWT Claims: " + jwt.getClaims());
            System.out.println("JWT Token Value (first 50 chars): " + jwt.getTokenValue().substring(0, Math.min(50, jwt.getTokenValue().length())));
            
            // Verificar que es un token JWT válido (debe empezar con eyJ)
            String tokenValue = jwt.getTokenValue();
            if (!tokenValue.startsWith("eyJ")) {
                System.out.println("❌ WARNING: Token no es JWT! Empieza con: " + tokenValue.substring(0, Math.min(10, tokenValue.length())));
            } else {
                System.out.println("✅ Token JWT válido detectado");
            }
            
            java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();
            
            // Hydra puede enviar scopes como string o como array
            Object scopeObj = jwt.getClaim("scope");
            Object scpObj = jwt.getClaim("scp");
            
            System.out.println("Scope claim: " + scopeObj);
            System.out.println("Scp claim: " + scpObj);
            
            // Manejar diferentes formatos de scopes
            if (scopeObj != null) {
                if (scopeObj instanceof String) {
                    // Scopes como string separado por espacios
                    String scopeStr = (String) scopeObj;
                    authorities.addAll(java.util.Arrays.stream(scopeStr.split(" "))
                        .filter(s -> !s.trim().isEmpty())
                        .map(s -> (org.springframework.security.core.GrantedAuthority) new org.springframework.security.core.authority.SimpleGrantedAuthority("SCOPE_" + s.trim()))
                        .collect(java.util.stream.Collectors.toList()));
                } else if (scopeObj instanceof java.util.List) {
                    // Scopes como array/lista
                    @SuppressWarnings("unchecked")
                    java.util.List<String> scopeList = (java.util.List<String>) scopeObj;
                    authorities.addAll(scopeList.stream()
                        .filter(s -> s != null && !s.trim().isEmpty())
                        .map(s -> (org.springframework.security.core.GrantedAuthority) new org.springframework.security.core.authority.SimpleGrantedAuthority("SCOPE_" + s.trim()))
                        .collect(java.util.stream.Collectors.toList()));
                }
            }
            
            // También revisar 'scp' como fallback
            if (authorities.isEmpty() && scpObj != null) {
                if (scpObj instanceof String) {
                    String scpStr = (String) scpObj;
                    authorities.addAll(java.util.Arrays.stream(scpStr.split(" "))
                        .filter(s -> !s.trim().isEmpty())
                        .map(s -> (org.springframework.security.core.GrantedAuthority) new org.springframework.security.core.authority.SimpleGrantedAuthority("SCOPE_" + s.trim()))
                        .collect(java.util.stream.Collectors.toList()));
                } else if (scpObj instanceof java.util.List) {
                    @SuppressWarnings("unchecked")
                    java.util.List<String> scpList = (java.util.List<String>) scpObj;
                    authorities.addAll(scpList.stream()
                        .filter(s -> s != null && !s.trim().isEmpty())
                        .map(s -> (org.springframework.security.core.GrantedAuthority) new org.springframework.security.core.authority.SimpleGrantedAuthority("SCOPE_" + s.trim()))
                        .collect(java.util.stream.Collectors.toList()));
                }
            }
            
            if (authorities.isEmpty()) {
                System.out.println("No scopes found!");
            }
            
            System.out.println("Generated authorities: " + authorities);
            System.out.println("=====================");
            
            return authorities;
        });
        
        return jwtConverter;
    }
}