/*
 * XML Type:  ResponseErrorAttributesType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ResponseErrorAttributesType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseErrorAttributesType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "responseerrorattributestype38f1type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "ResponseErrorAttribute" elements
     */
    java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType> getResponseErrorAttributeList();

    /**
     * Gets array of all "ResponseErrorAttribute" elements
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType[] getResponseErrorAttributeArray();

    /**
     * Gets ith "ResponseErrorAttribute" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType getResponseErrorAttributeArray(int i);

    /**
     * Returns number of "ResponseErrorAttribute" element
     */
    int sizeOfResponseErrorAttributeArray();

    /**
     * Sets array of all "ResponseErrorAttribute" element
     */
    void setResponseErrorAttributeArray(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType[] responseErrorAttributeArray);

    /**
     * Sets ith "ResponseErrorAttribute" element
     */
    void setResponseErrorAttributeArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType responseErrorAttribute);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ResponseErrorAttribute" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType insertNewResponseErrorAttribute(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "ResponseErrorAttribute" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType addNewResponseErrorAttribute();

    /**
     * Removes the ith "ResponseErrorAttribute" element
     */
    void removeResponseErrorAttribute(int i);
}
