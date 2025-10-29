package com.zelda.codex.gateways;

import com.zelda.codex.models.Weapon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import java.util.Map;

public interface IWeaponGateway {
    
    /**
     * Obtiene un arma por su ID desde el servicio SOAP
     */
    Weapon getWeaponById(UUID id);
    
    /**
     * Obtiene un listado paginado de armas con filtros opcionales
     */
    Page<Weapon> getAllWeapons(Pageable pageable, Map<String, String> filters);
    
    /**
     * Crea un arma nueva en el servicio SOAP
     */
    Weapon createWeapon(Weapon weapon);
    
    /**
     * Reemplaza completamente un arma en el servicio SOAP
     */
    Weapon replaceWeapon(UUID id, Weapon weapon);
    
    /**
     * Actualiza parcialmente un arma en el servicio SOAP
     */
    Weapon updateWeapon(UUID id, Map<String, Object> updates);
    
    /**
     * Elimina un arma del servicio SOAP
     */
    boolean deleteWeapon(UUID id);
}