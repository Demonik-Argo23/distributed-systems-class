//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.11.28 a las 09:07:34 PM CST 
//


package com.zelda.codex.soap;

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
 *         &lt;element name="weaponInput" type="{http://zelda.com/weapons}weaponInput"/&gt;
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
    "weaponInput"
})
@XmlRootElement(name = "createWeaponRequest")
public class CreateWeaponRequest {

    @XmlElement(required = true)
    protected WeaponInput weaponInput;

    /**
     * Obtiene el valor de la propiedad weaponInput.
     * 
     * @return
     *     possible object is
     *     {@link WeaponInput }
     *     
     */
    public WeaponInput getWeaponInput() {
        return weaponInput;
    }

    /**
     * Define el valor de la propiedad weaponInput.
     * 
     * @param value
     *     allowed object is
     *     {@link WeaponInput }
     *     
     */
    public void setWeaponInput(WeaponInput value) {
        this.weaponInput = value;
    }

}
