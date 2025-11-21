package com.zelda.codex.mappers;

import com.zelda.codex.dtos.CreateWeaponRequest;
import com.zelda.codex.dtos.ReplaceWeaponRequest;
import com.zelda.codex.dtos.UpdateWeaponRequest;
import com.zelda.codex.dtos.WeaponResponse;
import com.zelda.codex.models.Weapon;
import com.zelda.codex.models.Element;
import com.zelda.codex.models.WeaponType;
import com.zelda.codex.services.HateoasLinkService;
import com.zelda.codex.soap.WeaponInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class WeaponMapper {

    private final HateoasLinkService hateoasLinkService;

    @Autowired
    public WeaponMapper(HateoasLinkService hateoasLinkService) {
        this.hateoasLinkService = hateoasLinkService;
    }

    /**
     * Convierte un Weapon del modelo de dominio a WeaponResponse DTO
     */
    public WeaponResponse toResponse(Weapon weapon) {
        if (weapon == null) {
            return null;
        }

        WeaponResponse response = new WeaponResponse(
            weapon.getId(),
            weapon.getName(),
            weapon.getWeaponType(),
            weapon.getDamage(),
            weapon.getDurability(),
            weapon.getElement()
        );

        // Agregar enlaces HATEOAS
        if (weapon.getId() != null) {
            response.setLinks(hateoasLinkService.generateWeaponLinks(weapon.getId()));
        }

        return response;
    }

    /**
     * Convierte un CreateWeaponRequest DTO a Weapon del modelo de dominio
     */
    public Weapon toModel(CreateWeaponRequest request) {
        if (request == null) {
            return null;
        }

        Weapon weapon = new Weapon();
        weapon.setName(request.getName());
        weapon.setWeaponType(request.getWeaponType());
        weapon.setDamage(request.getDamage());
        weapon.setDurability(request.getDurability());
        weapon.setElement(request.getElement());
        
        return weapon;
    }

    /**
     * Convierte un ReplaceWeaponRequest DTO a Weapon del modelo de dominio
     */
    public Weapon toModel(ReplaceWeaponRequest request) {
        if (request == null) {
            return null;
        }

        Weapon weapon = new Weapon();
        weapon.setName(request.getName());
        weapon.setWeaponType(request.getWeaponType());
        weapon.setDamage(request.getDamage());
        weapon.setDurability(request.getDurability());
        weapon.setElement(request.getElement());
        
        return weapon;
    }

    /**
     * Convierte un UpdateWeaponRequest DTO a Map para actualizaciones parciales
     */
    public Map<String, Object> toUpdateMap(UpdateWeaponRequest request) {
        if (request == null) {
            return new HashMap<>();
        }

        Map<String, Object> updates = new HashMap<>();
        
        if (request.getName() != null) {
            updates.put("name", request.getName());
        }
        if (request.getWeaponType() != null) {
            updates.put("weaponType", request.getWeaponType());
        }
        if (request.getDamage() != null) {
            updates.put("damage", request.getDamage());
        }
        if (request.getDurability() != null) {
            updates.put("durability", request.getDurability());
        }
        if (request.getElement() != null) {
            updates.put("element", request.getElement());
        }

        return updates;
    }

    // ===== MÉTODOS DE CONVERSIÓN SOAP =====

    /**
     * Convierte de SOAP Weapon a modelo interno Weapon
     */
    public Weapon soapToModel(com.zelda.codex.soap.Weapon soapWeapon) {
        if (soapWeapon == null) {
            return null;
        }

        Weapon weapon = new Weapon();
        
        if (soapWeapon.getId() != null && !soapWeapon.getId().isEmpty()) {
            weapon.setId(UUID.fromString(soapWeapon.getId()));
        }
        
        weapon.setName(soapWeapon.getName());
        weapon.setWeaponType(mapSoapWeaponType(soapWeapon.getWeaponType()));
        weapon.setDamage(soapWeapon.getDamage());
        weapon.setDurability(soapWeapon.getDurability());
        weapon.setElement(mapSoapElement(soapWeapon.getElement()));

        return weapon;
    }

    /**
     * Convierte de modelo interno Weapon a SOAP WeaponInput
     */
    public WeaponInput modelToSoapInput(Weapon weapon) {
        if (weapon == null) {
            return null;
        }

        WeaponInput soapInput = new WeaponInput();
        soapInput.setName(weapon.getName());
        soapInput.setWeaponType(mapModelWeaponTypeToSoap(weapon.getWeaponType()));
        soapInput.setDamage(weapon.getDamage());
        soapInput.setDurability(weapon.getDurability());
        soapInput.setElement(mapModelElementToSoap(weapon.getElement()));

        return soapInput;
    }

    // Métodos auxiliares para mapear enums
    private WeaponType mapSoapWeaponType(com.zelda.codex.soap.WeaponType soapType) {
        if (soapType == null) return null;
        return WeaponType.valueOf(soapType.name());
    }

    private com.zelda.codex.soap.WeaponType mapModelWeaponTypeToSoap(WeaponType modelType) {
        if (modelType == null) return null;
        return com.zelda.codex.soap.WeaponType.valueOf(modelType.name());
    }

    private Element mapSoapElement(com.zelda.codex.soap.Element soapElement) {
        if (soapElement == null) return null;
        return Element.valueOf(soapElement.name());
    }

    private com.zelda.codex.soap.Element mapModelElementToSoap(Element modelElement) {
        if (modelElement == null) return null;
        return com.zelda.codex.soap.Element.valueOf(modelElement.name());
    }
}