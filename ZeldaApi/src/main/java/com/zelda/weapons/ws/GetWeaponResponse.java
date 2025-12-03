//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.12.02 a las 07:22:31 PM CST 
//


package com.zelda.weapons.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="weapon" type="{http://zelda.com/weapons}weapon"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "weapon"
})
@XmlRootElement(name = "getWeaponResponse")
public class GetWeaponResponse {

    @XmlElement(required = true)
    protected Weapon weapon;

    /**
     * Obtiene el valor de la propiedad weapon.
     * 
     * @return
     *     possible object is
     *     {@link Weapon }
     *     
     */
    public Weapon getWeapon() {
        return weapon;
    }

    /**
     * Define el valor de la propiedad weapon.
     * 
     * @param value
     *     allowed object is
     *     {@link Weapon }
     *     
     */
    public void setWeapon(Weapon value) {
        this.weapon = value;
    }

}
