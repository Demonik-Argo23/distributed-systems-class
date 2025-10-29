package com.zelda.codex.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.zelda.codex.models.Weapon;

public interface IWeaponService {
    
    /**
     * Obtiene un arma por su ID
     */
    Weapon getWeaponById(UUID id);
    
    /**
     * Obtiene un listado paginado de armas con filtros opcionales
     */
    Page<Weapon> getAllWeapons(Pageable pageable, Map<String, String> filters);
    
    /**
     * Crea un arma nueva
     */
    Weapon createWeapon(Weapon weapon);
    
    /**
     * Reemplaza completamente un arma existente
     */
    Weapon replaceWeapon(UUID id, Weapon weapon);
    
    /**
     * Actualiza parcialmente un arma
     */
    Weapon updateWeapon(UUID id, Map<String, Object> updates);
    
    /**
     * Elimina un arma
     */
    void deleteWeapon(UUID id);
}