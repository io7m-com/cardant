/*
 * An XML document type.
 * Localname: CommandTagsDelete
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandTagsDelete(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandTagsDeleteDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "commandtagsdelete57aadoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandTagsDelete" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType getCommandTagsDelete();

    /**
     * Sets the "CommandTagsDelete" element
     */
    void setCommandTagsDelete(com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType commandTagsDelete);

    /**
     * Appends and returns a new empty "CommandTagsDelete" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType addNewCommandTagsDelete();
}
