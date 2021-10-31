/*
 * XML Type:  FileType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.FileType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML FileType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface FileType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.FileType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "filetype2dc1type");
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
     * True if has "FileData" element
     */
    boolean isSetFileData();

    /**
     * Sets the "FileData" element
     */
    void setFileData(byte[] fileData);

    /**
     * Sets (as xml) the "FileData" element
     */
    void xsetFileData(com.io7m.cardant.protocol.inventory.v1.beans.FileDataType fileData);

    /**
     * Unsets the "FileData" element
     */
    void unsetFileData();

    /**
     * Gets the "id" attribute
     */
    java.lang.String getId();

    /**
     * Gets (as xml) the "id" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetId();

    /**
     * Sets the "id" attribute
     */
    void setId(java.lang.String id);

    /**
     * Sets (as xml) the "id" attribute
     */
    void xsetId(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType id);

    /**
     * Gets the "description" attribute
     */
    java.lang.String getDescription();

    /**
     * Gets (as xml) the "description" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.FileDescriptionType xgetDescription();

    /**
     * Sets the "description" attribute
     */
    void setDescription(java.lang.String description);

    /**
     * Sets (as xml) the "description" attribute
     */
    void xsetDescription(com.io7m.cardant.protocol.inventory.v1.beans.FileDescriptionType description);

    /**
     * Gets the "mediaType" attribute
     */
    java.lang.String getMediaType();

    /**
     * Gets (as xml) the "mediaType" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.MediaType xgetMediaType();

    /**
     * Sets the "mediaType" attribute
     */
    void setMediaType(java.lang.String mediaType);

    /**
     * Sets (as xml) the "mediaType" attribute
     */
    void xsetMediaType(com.io7m.cardant.protocol.inventory.v1.beans.MediaType mediaType);

    /**
     * Gets the "size" attribute
     */
    java.math.BigInteger getSize();

    /**
     * Gets (as xml) the "size" attribute
     */
    org.apache.xmlbeans.XmlUnsignedLong xgetSize();

    /**
     * Sets the "size" attribute
     */
    void setSize(java.math.BigInteger size);

    /**
     * Sets (as xml) the "size" attribute
     */
    void xsetSize(org.apache.xmlbeans.XmlUnsignedLong size);

    /**
     * Gets the "hashAlgorithm" attribute
     */
    java.lang.String getHashAlgorithm();

    /**
     * Gets (as xml) the "hashAlgorithm" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.HashAlgorithmType xgetHashAlgorithm();

    /**
     * Sets the "hashAlgorithm" attribute
     */
    void setHashAlgorithm(java.lang.String hashAlgorithm);

    /**
     * Sets (as xml) the "hashAlgorithm" attribute
     */
    void xsetHashAlgorithm(com.io7m.cardant.protocol.inventory.v1.beans.HashAlgorithmType hashAlgorithm);

    /**
     * Gets the "hashValue" attribute
     */
    java.lang.String getHashValue();

    /**
     * Gets (as xml) the "hashValue" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.HashValueType xgetHashValue();

    /**
     * Sets the "hashValue" attribute
     */
    void setHashValue(java.lang.String hashValue);

    /**
     * Sets (as xml) the "hashValue" attribute
     */
    void xsetHashValue(com.io7m.cardant.protocol.inventory.v1.beans.HashValueType hashValue);
}
