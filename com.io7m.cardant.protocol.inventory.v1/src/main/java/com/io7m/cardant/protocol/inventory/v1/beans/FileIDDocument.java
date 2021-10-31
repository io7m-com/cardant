/*
 * An XML document type.
 * Localname: FileID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.FileIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one FileID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface FileIDDocument extends com.io7m.cardant.protocol.inventory.v1.beans.IDDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.FileIDDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "fileid1ed0doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "FileID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.FileIDType getFileID();

    /**
     * Sets the "FileID" element
     */
    void setFileID(com.io7m.cardant.protocol.inventory.v1.beans.FileIDType fileID);

    /**
     * Appends and returns a new empty "FileID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.FileIDType addNewFileID();
}
