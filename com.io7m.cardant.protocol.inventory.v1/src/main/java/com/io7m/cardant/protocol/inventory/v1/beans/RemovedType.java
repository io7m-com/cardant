/*
 * XML Type:  RemovedType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.RemovedType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML RemovedType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface RemovedType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.RemovedType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "removedtypececftype");
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
