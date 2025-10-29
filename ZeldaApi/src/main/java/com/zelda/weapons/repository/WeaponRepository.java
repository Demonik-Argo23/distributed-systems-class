package com.zelda.weapons.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zelda.weapons.model.Weapon;


@Repository
public interface WeaponRepository extends JpaRepository<Weapon, UUID> {
    
    //JPA ya incluye las operaciones básicas para laa app SOAP, no es necesario añadir mas metodos, por eso se ve todo vacío jajaj
}