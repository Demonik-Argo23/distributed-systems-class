package com.zelda.codex.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.zelda.codex.dtos.Link;

@Service
public class HateoasLinkService {

    private static final String WEAPONS_BASE_PATH = "/api/v1/weapons";

    public List<Link> generateWeaponLinks(UUID weaponId) {
        List<Link> links = new ArrayList<>();
        String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();

        links.add(new Link("self", 
            baseUri + WEAPONS_BASE_PATH + "/" + weaponId, 
            "GET"));

        links.add(new Link("update", 
            baseUri + WEAPONS_BASE_PATH + "/" + weaponId, 
            "PUT"));

        links.add(new Link("patch", 
            baseUri + WEAPONS_BASE_PATH + "/" + weaponId, 
            "PATCH"));

        // Delete link
        links.add(new Link("delete", 
            baseUri + WEAPONS_BASE_PATH + "/" + weaponId, 
            "DELETE"));

        // Collection link
        links.add(new Link("collection", 
            baseUri + WEAPONS_BASE_PATH, 
            "GET"));

        return links;
    }

    public List<Link> generateCollectionLinks() {
        List<Link> links = new ArrayList<>();
        String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();

        links.add(new Link("self", 
            baseUri + WEAPONS_BASE_PATH, 
            "GET"));

        links.add(new Link("create", 
            baseUri + WEAPONS_BASE_PATH, 
            "POST"));

        return links;
    }

    public String generateLocationHeader(UUID weaponId) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(weaponId)
            .toString();
    }
}