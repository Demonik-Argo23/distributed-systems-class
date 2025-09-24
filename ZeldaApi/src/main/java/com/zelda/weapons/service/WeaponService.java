package com.zelda.weapons.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zelda.weapons.model.Weapon;
import com.zelda.weapons.repository.WeaponRepository;
import com.zelda.weapons.validator.WeaponValidator;

import jakarta.validation.Valid;

@Service
@Transactional
public class WeaponService {

    private final WeaponRepository weaponRepository;
    private final WeaponValidator weaponValidator;

    @Autowired
    public WeaponService(WeaponRepository weaponRepository, WeaponValidator weaponValidator) {
        this.weaponRepository = weaponRepository;
        this.weaponValidator = weaponValidator;
    }

    public Weapon createWeapon(@Valid Weapon weapon) {
        weaponValidator.validateWeaponData(weapon);
        weapon.setId(null);
        return weaponRepository.save(weapon);
    }

    @Transactional(readOnly = true)
    public Weapon getWeaponById(Long id) {
        weaponValidator.validateWeaponId(id);

        return weaponRepository.findById(id)
                .orElseThrow(() -> new WeaponNotFoundException("Arma con ID " + id + " no encontrada"));
    }

    public boolean deleteWeapon(Long id) {
        weaponValidator.validateWeaponId(id);

        if (!weaponRepository.existsById(id)) {
            throw new WeaponNotFoundException("Arma con ID " + id + " no encontrada");
        }

        weaponRepository.deleteById(id);
        return true;
    }

    public static class WeaponNotFoundException extends RuntimeException {
        public WeaponNotFoundException(String message) {
            super(message);
        }
    }
}