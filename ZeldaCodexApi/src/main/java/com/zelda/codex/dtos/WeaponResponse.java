package com.zelda.codex.dtos;

import com.zelda.codex.models.Element;
import com.zelda.codex.models.WeaponType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WeaponResponse {
    
    private UUID id;
    private String name;
    private WeaponType weaponType;
    private Integer damage;
    private Integer durability;
    private Element element;
    
    @Schema(description = "Enlaces HATEOAS relacionados con este recurso")
    private List<Link> links = new ArrayList<>();

    // Constructor vacío
    public WeaponResponse() {}

    // Constructor completo
    public WeaponResponse(UUID id, String name, WeaponType weaponType, Integer damage, Integer durability, Element element) {
        this.id = id;
        this.name = name;
        this.weaponType = weaponType;
        this.damage = damage;
        this.durability = durability;
        this.element = element;
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    public Integer getDamage() {
        return damage;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public Integer getDurability() {
        return durability;
    }

    public void setDurability(Integer durability) {
        this.durability = durability;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public void addLink(Link link) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(link);
    }
}