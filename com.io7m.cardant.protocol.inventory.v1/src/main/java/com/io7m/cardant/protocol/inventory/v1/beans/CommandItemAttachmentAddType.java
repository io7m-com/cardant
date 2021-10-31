/*
 * XML Type:  CommandItemAttachmentAddType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML CommandItemAttachmentAddType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandItemAttachmentAddType extends com.io7m.cardant.protocol.inventory.v1.beans.CommandType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "commanditemattachmentaddtype8c1dtype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "item" attribute
     */
    java.lang.String getItem();

    /**
     * Gets (as xml) the "item" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetItem();

    /**
     * Sets the "item" attribute
     */
    void setItem(java.lang.String item);

    /**
     * Sets (as xml) the "item" attribute
     */
    void xsetItem(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType item);

    /**
     * Gets the "file" attribute
     */
    java.lang.String getFile();

    /**
     * Gets (as xml) the "file" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetFile();

    /**
     * Sets the "file" attribute
     */
    void setFile(java.lang.String file);

    /**
     * Sets (as xml) the "file" attribute
     */
    void xsetFile(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType file);

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
