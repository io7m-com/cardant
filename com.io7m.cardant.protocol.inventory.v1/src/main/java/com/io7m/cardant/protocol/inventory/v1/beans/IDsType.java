/*
 * XML Type:  IDsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.IDsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML IDsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface IDsType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.IDsType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "idstypeebd7type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "ID" elements
     */
    java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.IDType> getIDList();

    /**
     * Gets array of all "ID" elements
     */
    com.io7m.cardant.protocol.inventory.v1.beans.IDType[] getIDArray();

    /**
     * Gets ith "ID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.IDType getIDArray(int i);

    /**
     * Returns number of "ID" element
     */
    int sizeOfIDArray();

    /**
     * Sets array of all "ID" element
     */
    void setIDArray(com.io7m.cardant.protocol.inventory.v1.beans.IDType[] idArray);

    /**
     * Sets ith "ID" element
     */
    void setIDArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.IDType id);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.IDType insertNewID(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "ID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.IDType addNewID();

    /**
     * Removes the ith "ID" element
     */
    void removeID(int i);
}
