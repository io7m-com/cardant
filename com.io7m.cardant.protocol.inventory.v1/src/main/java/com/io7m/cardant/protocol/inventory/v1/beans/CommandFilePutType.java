/*
 * XML Type:  CommandFilePutType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML CommandFilePutType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandFilePutType extends com.io7m.cardant.protocol.inventory.v1.beans.CommandType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "commandfileputtype0bd5type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "File" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.FileType getFile();

    /**
     * Sets the "File" element
     */
    void setFile(com.io7m.cardant.protocol.inventory.v1.beans.FileType file);

    /**
     * Appends and returns a new empty "File" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.FileType addNewFile();
}
