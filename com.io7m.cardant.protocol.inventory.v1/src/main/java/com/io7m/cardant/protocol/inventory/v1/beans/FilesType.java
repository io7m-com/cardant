/*
 * XML Type:  FilesType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.FilesType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML FilesType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface FilesType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.FilesType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "filestype2d58type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "File" elements
     */
    java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.FileType> getFileList();

    /**
     * Gets array of all "File" elements
     */
    com.io7m.cardant.protocol.inventory.v1.beans.FileType[] getFileArray();

    /**
     * Gets ith "File" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.FileType getFileArray(int i);

    /**
     * Returns number of "File" element
     */
    int sizeOfFileArray();

    /**
     * Sets array of all "File" element
     */
    void setFileArray(com.io7m.cardant.protocol.inventory.v1.beans.FileType[] fileArray);

    /**
     * Sets ith "File" element
     */
    void setFileArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.FileType file);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "File" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.FileType insertNewFile(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "File" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.FileType addNewFile();

    /**
     * Removes the ith "File" element
     */
    void removeFile(int i);
}
