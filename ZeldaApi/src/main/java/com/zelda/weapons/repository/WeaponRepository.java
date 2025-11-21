package com.zelda.weapons.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zelda.weapons.model.Weapon;


@Repository
public interface WeaponRepository extends JpaRepository<Weapon, UUID> {
    
    //JPA ya incluye las operaciones básicas para laa app SOAP, no es necesario añadir mas metodos, por eso se ve todo vacío jajaj
    
    /**
     * Busca un arma por su nombre (ignorando mayúsculas/minúsculas)
     * @param name Nombre del arma
     * @return true si existe un arma con ese nombre, false en caso contrario
     */
    @Query("SELECT COUNT(w) > 0 FROM Weapon w WHERE UPPER(w.name) = UPPER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);
}