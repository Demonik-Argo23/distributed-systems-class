package com.zelda.weapons.mapper;

import org.springframework.stereotype.Component;

@Component
public class WeaponMapper {

    public com.zelda.weapons.ws.Weapon entityToSoap(com.zelda.weapons.model.Weapon entityWeapon) {
        if (entityWeapon == null) {
            return null;
        }

        com.zelda.weapons.ws.Weapon soapWeapon = new com.zelda.weapons.ws.Weapon();
        soapWeapon.setId(entityWeapon.getId());
        soapWeapon.setName(entityWeapon.getName());
        soapWeapon.setType(mapWeaponTypeToSoap(entityWeapon.getWeaponType()));
        soapWeapon.setDamage(entityWeapon.getDamage());
        soapWeapon.setDurability(entityWeapon.getDurability());
        soapWeapon.setElement(mapElementToSoap(entityWeapon.getElement()));

        return soapWeapon;
    }

    public com.zelda.weapons.model.Weapon soapInputToEntity(com.zelda.weapons.ws.WeaponInput weaponInput) {
        if (weaponInput == null) {
            return null;
        }

        com.zelda.weapons.model.Weapon entityWeapon = new com.zelda.weapons.model.Weapon();
        entityWeapon.setName(weaponInput.getName());
        entityWeapon.setWeaponType(mapWeaponTypeToEntity(weaponInput.getType()));
        entityWeapon.setDamage(weaponInput.getDamage());
        entityWeapon.setDurability(weaponInput.getDurability());
        entityWeapon.setElement(mapElementToEntity(weaponInput.getElement()));

        return entityWeapon;
    }

    private com.zelda.weapons.ws.WeaponType mapWeaponTypeToSoap(com.zelda.weapons.enums.WeaponType entityType) {
        if (entityType == null) {
            return null;
        }

        return switch (entityType) {
            case Bat -> com.zelda.weapons.ws.WeaponType.BAT;
            case Boomerang -> com.zelda.weapons.ws.WeaponType.BOOMERANG;
            case Club -> com.zelda.weapons.ws.WeaponType.CLUB;
            case Hammer -> com.zelda.weapons.ws.WeaponType.HAMMER;
            case Rod -> com.zelda.weapons.ws.WeaponType.ROD;
            case Spear -> com.zelda.weapons.ws.WeaponType.SPEAR;
            case OneHandedSword -> com.zelda.weapons.ws.WeaponType.ONE_HANDED_SWORD;
            case TwoHandedSword -> com.zelda.weapons.ws.WeaponType.TWO_HANDED_SWORD;
            case Other -> com.zelda.weapons.ws.WeaponType.OTHER;
        };
    }

    private com.zelda.weapons.enums.WeaponType mapWeaponTypeToEntity(com.zelda.weapons.ws.WeaponType soapType) {
        if (soapType == null) {
            return null;
        }

        return switch (soapType) {
            case BAT -> com.zelda.weapons.enums.WeaponType.Bat;
            case BOOMERANG -> com.zelda.weapons.enums.WeaponType.Boomerang;
            case CLUB -> com.zelda.weapons.enums.WeaponType.Club;
            case HAMMER -> com.zelda.weapons.enums.WeaponType.Hammer;
            case ROD -> com.zelda.weapons.enums.WeaponType.Rod;
            case SPEAR -> com.zelda.weapons.enums.WeaponType.Spear;
            case ONE_HANDED_SWORD -> com.zelda.weapons.enums.WeaponType.OneHandedSword;
            case TWO_HANDED_SWORD -> com.zelda.weapons.enums.WeaponType.TwoHandedSword;
            case OTHER -> com.zelda.weapons.enums.WeaponType.Other;
        };
    }

    private com.zelda.weapons.ws.Element mapElementToSoap(com.zelda.weapons.enums.Element entityElement) {
        if (entityElement == null) {
            return null;
        }

        return switch (entityElement) {
            case Fire -> com.zelda.weapons.ws.Element.FIRE;
            case Ice -> com.zelda.weapons.ws.Element.ICE;
            case Electric -> com.zelda.weapons.ws.Element.ELECTRIC;
        };
    }

    private com.zelda.weapons.enums.Element mapElementToEntity(com.zelda.weapons.ws.Element soapElement) {
        if (soapElement == null) {
            return null;
        }

        return switch (soapElement) {
            case FIRE -> com.zelda.weapons.enums.Element.Fire;
            case ICE -> com.zelda.weapons.enums.Element.Ice;
            case ELECTRIC -> com.zelda.weapons.enums.Element.Electric;
        };
    }
}