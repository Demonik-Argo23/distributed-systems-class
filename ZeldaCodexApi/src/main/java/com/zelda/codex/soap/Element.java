//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.12.02 a las 02:30:03 PM CST 
//


package com.zelda.codex.soap;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para element.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="element"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="FIRE"/&gt;
 *     &lt;enumeration value="ICE"/&gt;
 *     &lt;enumeration value="LIGHTNING"/&gt;
 *     &lt;enumeration value="NONE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "element")
@XmlEnum
public enum Element {

    FIRE,
    ICE,
    LIGHTNING,
    NONE;

    public String value() {
        return name();
    }

    public static Element fromValue(String v) {
        return valueOf(v);
    }

}
