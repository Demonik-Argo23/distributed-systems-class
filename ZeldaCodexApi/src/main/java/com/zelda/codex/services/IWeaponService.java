package com.zelda.codex.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.zelda.codex.models.Weapon;

public interface IWeaponService {

    Weapon getWeaponById(UUID id);

    Page<Weapon> getAllWeapons(Pageable pageable, Map<String, String> filters);

    Weapon createWeapon(Weapon weapon);

    Weapon replaceWeapon(UUID id, Weapon weapon);

    Weapon updateWeapon(UUID id, Map<String, Object> updates);

    void deleteWeapon(UUID id);
}