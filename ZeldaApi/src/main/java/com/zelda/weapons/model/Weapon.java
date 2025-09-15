package com.zelda.weapons.model;

import com.zelda.weapons.enums.Element;
import com.zelda.weapons.enums.WeaponType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "weapons")
public class Weapon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del arma es obligatorio")
    @Column(name = "Name", nullable = false, length = 100)
    private String name;
    
    @NotNull(message = "El tipo de arma es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "WeaponType", nullable = false)
    private WeaponType weaponType;
    
    @Min(value = 1, message = "El daño debe ser mayor a 0")
    @Column(name = "Damage", nullable = false)
    private Integer damage;
    
    @Min(value = 1, message = "La durabilidad debe ser mayor a 0")
    @Column(name = "Durability", nullable = false)
    private Integer durability;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "Element")
    private Element element;
    
    
    public Weapon() {

    }
    
    public Weapon(String name, WeaponType weaponType, Integer damage, Integer durability, Element element) {
        this.name = name;
        this.weaponType = weaponType;
        this.damage = damage;
        this.durability = durability;
        this.element = element;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public WeaponType getWeaponType() {
        return weaponType;
    }
    
    public void setWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }
    
    public Integer getDamage() {
        return damage;
    }
    
    public void setDamage(Integer damage) {
        this.damage = damage;
    }
    
    public Integer getDurability() {
        return durability;
    }
    
    public void setDurability(Integer durability) {
        this.durability = durability;
    }
    
    public Element getElement() {
        return element;
    }
    
    public void setElement(Element element) {
        this.element = element;
    }
    
    @Override
    public String toString() {
        return "Weapon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", weaponType=" + weaponType +
                ", damage=" + damage +
                ", durability=" + durability +
                ", element=" + element +
                '}';
    }
}
