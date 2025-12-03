package com.zelda.weapons.mapper;

import org.springframework.stereotype.Component;

@Component
public class WeaponMapper {

    public com.zelda.weapons.ws.Weapon entityToSoap(com.zelda.weapons.model.Weapon entityWeapon) {
        if (entityWeapon == null) {
            return null;
        }

        com.zelda.weapons.ws.Weapon soapWeapon = new com.zelda.weapons.ws.Weapon();
        soapWeapon.setId(entityWeapon.getId().toString());
        soapWeapon.setName(entityWeapon.getName());
        soapWeapon.setWeaponType(mapWeaponTypeToSoap(entityWeapon.getWeaponType()));
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
        entityWeapon.setWeaponType(mapWeaponTypeToEntity(weaponInput.getWeaponType()));
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
            case ONE_HANDED_SWORD -> com.zelda.weapons.ws.WeaponType.ONE_HANDED_SWORD;
            case TWO_HANDED_SWORD -> com.zelda.weapons.ws.WeaponType.TWO_HANDED_SWORD;
            case SPEAR -> com.zelda.weapons.ws.WeaponType.SPEAR;
            case BOW -> com.zelda.weapons.ws.WeaponType.BOW;
            case SHIELD -> com.zelda.weapons.ws.WeaponType.SHIELD;
        };
    }

    private com.zelda.weapons.enums.WeaponType mapWeaponTypeToEntity(com.zelda.weapons.ws.WeaponType soapType) {
        if (soapType == null) {
            return null;
        }

        return switch (soapType) {
            case ONE_HANDED_SWORD -> com.zelda.weapons.enums.WeaponType.ONE_HANDED_SWORD;
            case TWO_HANDED_SWORD -> com.zelda.weapons.enums.WeaponType.TWO_HANDED_SWORD;
            case SPEAR -> com.zelda.weapons.enums.WeaponType.SPEAR;
            case BOW -> com.zelda.weapons.enums.WeaponType.BOW;
            case SHIELD -> com.zelda.weapons.enums.WeaponType.SHIELD;
        };
    }

    private com.zelda.weapons.ws.Element mapElementToSoap(com.zelda.weapons.enums.Element entityElement) {
        if (entityElement == null) {
            return null;
        }

        return switch (entityElement) {
            case FIRE -> com.zelda.weapons.ws.Element.FIRE;
            case ICE -> com.zelda.weapons.ws.Element.ICE;
            case LIGHTNING -> com.zelda.weapons.ws.Element.LIGHTNING;
            case NONE -> com.zelda.weapons.ws.Element.NONE;
        };
    }

    private com.zelda.weapons.enums.Element mapElementToEntity(com.zelda.weapons.ws.Element soapElement) {
        if (soapElement == null) {
            return null;
        }

        return switch (soapElement) {
            case FIRE -> com.zelda.weapons.enums.Element.FIRE;
            case ICE -> com.zelda.weapons.enums.Element.ICE;
            case LIGHTNING -> com.zelda.weapons.enums.Element.LIGHTNING;
            case NONE -> com.zelda.weapons.enums.Element.NONE;
        };
    }
}