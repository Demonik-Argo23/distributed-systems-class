//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.11.01 a las 12:14:56 AM CST 
//


package com.zelda.weapons.ws;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para weaponType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="weaponType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ONE_HANDED_SWORD"/&gt;
 *     &lt;enumeration value="TWO_HANDED_SWORD"/&gt;
 *     &lt;enumeration value="SPEAR"/&gt;
 *     &lt;enumeration value="BOW"/&gt;
 *     &lt;enumeration value="SHIELD"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "weaponType")
@XmlEnum
public enum WeaponType {

    ONE_HANDED_SWORD,
    TWO_HANDED_SWORD,
    SPEAR,
    BOW,
    SHIELD;

    public String value() {
        return name();
    }

    public static WeaponType fromValue(String v) {
        return valueOf(v);
    }

}
