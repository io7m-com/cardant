/*
 * An XML document type.
 * Localname: CommandTagList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandTagList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandTagListDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "commandtaglist089adoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandTagList" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListType getCommandTagList();

    /**
     * Sets the "CommandTagList" element
     */
    void setCommandTagList(com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListType commandTagList);

    /**
     * Appends and returns a new empty "CommandTagList" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListType addNewCommandTagList();
}
