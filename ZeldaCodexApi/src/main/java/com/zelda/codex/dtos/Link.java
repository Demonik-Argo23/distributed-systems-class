package com.zelda.codex.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enlace HATEOAS")
public class Link {
    
    @Schema(description = "Relación del enlace", example = "self")
    private String rel;
    
    @Schema(description = "URL del enlace", example = "/api/v1/weapons/123e4567-e89b-12d3-a456-426614174000")
    private String href;
    
    @Schema(description = "Método HTTP", example = "GET")
    private String method;

    public Link() {}

    public Link(String rel, String href, String method) {
        this.rel = rel;
        this.href = href;
        this.method = method;
    }

    // Getters y setters
    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}