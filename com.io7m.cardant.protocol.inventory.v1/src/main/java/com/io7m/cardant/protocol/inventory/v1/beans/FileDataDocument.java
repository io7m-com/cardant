/*
 * An XML document type.
 * Localname: FileData
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.FileDataDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one FileData(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface FileDataDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.FileDataDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "filedataaf41doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "FileData" element
     */
    byte[] getFileData();

    /**
     * Gets (as xml) the "FileData" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.FileDataType xgetFileData();

    /**
     * Sets the "FileData" element
     */
    void setFileData(byte[] fileData);

    /**
     * Sets (as xml) the "FileData" element
     */
    void xsetFileData(com.io7m.cardant.protocol.inventory.v1.beans.FileDataType fileData);
}
