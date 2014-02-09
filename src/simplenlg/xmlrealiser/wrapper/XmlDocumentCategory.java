//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.10.04 at 10:45:24 PM CEST 
//


package simplenlg.xmlrealiser.wrapper;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for documentCategory.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="documentCategory">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DOCUMENT"/>
 *     &lt;enumeration value="SECTION"/>
 *     &lt;enumeration value="PARAGRAPH"/>
 *     &lt;enumeration value="SENTENCE"/>
 *     &lt;enumeration value="LIST"/>
 *     &lt;enumeration value="LIST_ITEM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "documentCategory")
@XmlEnum
public enum XmlDocumentCategory {

    DOCUMENT,
    SECTION,
    PARAGRAPH,
    SENTENCE,
    LIST,
    LIST_ITEM;

    public String value() {
        return name();
    }

    public static XmlDocumentCategory fromValue(String v) {
        return valueOf(v);
    }

}
