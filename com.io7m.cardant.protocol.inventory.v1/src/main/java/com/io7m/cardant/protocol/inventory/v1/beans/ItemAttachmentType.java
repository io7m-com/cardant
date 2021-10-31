/*
 * XML Type:  ItemAttachmentType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ItemAttachmentType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemAttachmentType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "itemattachmenttypee627type");
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

    /**
     * Gets the "relation" attribute
     */
    java.lang.String getRelation();

    /**
     * Gets (as xml) the "relation" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.RelationType xgetRelation();

    /**
     * Sets the "relation" attribute
     */
    void setRelation(java.lang.String relation);

    /**
     * Sets (as xml) the "relation" attribute
     */
    void xsetRelation(com.io7m.cardant.protocol.inventory.v1.beans.RelationType relation);
}
