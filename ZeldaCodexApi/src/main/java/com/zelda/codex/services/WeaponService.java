package com.zelda.codex.services;

import com.zelda.codex.exceptions.WeaponNotFoundException;
import com.zelda.codex.gateways.IWeaponGateway;
import com.zelda.codex.models.Weapon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class WeaponService implements IWeaponService {

    private final IWeaponGateway weaponGateway;

    @Autowired
    public WeaponService(IWeaponGateway weaponGateway) {
        this.weaponGateway = weaponGateway;
    }

    @Override
    public Weapon getWeaponById(UUID id) {
        return weaponGateway.getWeaponById(id);
    }

    @Override
    public Page<Weapon> getAllWeapons(Pageable pageable, Map<String, String> filters) {
        return weaponGateway.getAllWeapons(pageable, filters);
    }

    @Override
    public Weapon createWeapon(Weapon weapon) {
        return weaponGateway.createWeapon(weapon);
    }

    @Override
    public Weapon replaceWeapon(UUID id, Weapon weapon) {
        weapon.setId(id); // Asegurar que el ID coincida con la URL
        return weaponGateway.replaceWeapon(id, weapon);
    }

    @Override
    public Weapon updateWeapon(UUID id, Map<String, Object> updates) {
        return weaponGateway.updateWeapon(id, updates);
    }

    @Override
    public void deleteWeapon(UUID id) {
        boolean deleted = weaponGateway.deleteWeapon(id);
        if (!deleted) {
            throw new WeaponNotFoundException(id);
        }
    }
}