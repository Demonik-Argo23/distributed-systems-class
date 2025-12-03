package com.zelda.codex.services;

import com.zelda.codex.exceptions.WeaponNotFoundException;
import com.zelda.codex.gateways.IWeaponGateway;
import com.zelda.codex.models.Weapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class WeaponService implements IWeaponService {

    private static final Logger logger = LoggerFactory.getLogger(WeaponService.class);

    private final IWeaponGateway weaponGateway;

    @Autowired
    public WeaponService(IWeaponGateway weaponGateway) {
        this.weaponGateway = weaponGateway;
    }

    @Override
    @Cacheable(value = "weapons", key = "#id")
    public Weapon getWeaponById(UUID id) {
        logger.info("Buscando arma con ID {} - Cache MISS", id);
        Weapon weapon = weaponGateway.getWeaponById(id);
        logger.debug("Arma {} encontrada y guardada en cache", weapon.getName());
        return weapon;
    }

    @Override
    @Cacheable(
        value = "weaponsList", 
        key = "T(String).format('%d_%d_%s_%s', #pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString(), #filters.toString())",
        unless = "#result == null || #result.isEmpty()"
    )
    public Page<Weapon> getAllWeapons(Pageable pageable, Map<String, String> filters) {
        logger.info("Obteniendo lista de armas - Página {}, Tamaño {}, Filtros {} - Cache MISS", 
                   pageable.getPageNumber(), pageable.getPageSize(), filters);
        Page<Weapon> weapons = weaponGateway.getAllWeapons(pageable, filters);
        logger.debug("Lista de {} armas obtenida y guardada en cache", weapons.getContent().size());
        return weapons;
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "weaponsList", allEntries = true),
        @CacheEvict(value = "weapons", key = "#result.id", condition = "#result != null")
    })
    public Weapon createWeapon(Weapon weapon) {
        logger.info("Creando nueva arma: {} - Invalidando cache", weapon.getName());
        Weapon createdWeapon = weaponGateway.createWeapon(weapon);
        logger.debug("Arma {} creada exitosamente - Cache invalidado", createdWeapon.getName());
        return createdWeapon;
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "weapons", key = "#id"),
        @CacheEvict(value = "weaponsList", allEntries = true)
    })
    public Weapon replaceWeapon(UUID id, Weapon weapon) {
        logger.info("Reemplazando arma con ID {} - Invalidando cache", id);
        weapon.setId(id); // Asegurar que el ID coincida con la URL
        Weapon replacedWeapon = weaponGateway.replaceWeapon(id, weapon);
        logger.debug("Arma {} reemplazada exitosamente - Cache invalidado", replacedWeapon.getName());
        return replacedWeapon;
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "weapons", key = "#id"),
        @CacheEvict(value = "weaponsList", allEntries = true)
    })
    public Weapon updateWeapon(UUID id, Map<String, Object> updates) {
        logger.info("Actualizando arma con ID {} - Invalidando cache", id);
        Weapon updatedWeapon = weaponGateway.updateWeapon(id, updates);
        logger.debug("Arma actualizada exitosamente - Cache invalidado");
        return updatedWeapon;
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "weapons", key = "#id"),
        @CacheEvict(value = "weaponsList", allEntries = true)
    })
    public void deleteWeapon(UUID id) {
        logger.info("Eliminando arma con ID {} - Invalidando cache", id);
        boolean deleted = weaponGateway.deleteWeapon(id);
        if (!deleted) {
            throw new WeaponNotFoundException(id);
        }
        logger.debug("Arma con ID {} eliminada exitosamente - Cache invalidado", id);
    }
}