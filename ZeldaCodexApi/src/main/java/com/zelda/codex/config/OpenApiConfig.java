package com.zelda.codex.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@SecurityScheme(name = "oauth2", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(clientCredentials = @OAuthFlow(tokenUrl = "http://localhost:4444/oauth2/token", scopes = {
        @OAuthScope(name = "read", description = "Permite leer información de armas"),
        @OAuthScope(name = "write", description = "Permite crear, actualizar y eliminar armas")
}), authorizationCode = @OAuthFlow(authorizationUrl = "http://localhost:4444/oauth2/auth", tokenUrl = "http://localhost:4444/oauth2/token", scopes = {
        @OAuthScope(name = "read", description = "Permite leer información de armas"),
        @OAuthScope(name = "write", description = "Permite crear, actualizar y eliminar armas")
})))
public class OpenApiConfig {

    @Value("${spring.application.name:Zelda Codex API}")
    private String appName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Zelda Codex API")
                        .description(
                                "API REST Gateway para el Codex de armas de Zelda Breath of the Wild con autenticación OAuth2")
                        .version("v1.0.0"));
    }
}