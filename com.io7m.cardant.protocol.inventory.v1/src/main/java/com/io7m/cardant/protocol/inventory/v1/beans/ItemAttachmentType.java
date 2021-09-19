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
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "itemattachmenttypee627type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemAttachmentData" element
     */
    byte[] getItemAttachmentData();

    /**
     * Gets (as xml) the "ItemAttachmentData" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType xgetItemAttachmentData();

    /**
     * True if has "ItemAttachmentData" element
     */
    boolean isSetItemAttachmentData();

    /**
     * Sets the "ItemAttachmentData" element
     */
    void setItemAttachmentData(byte[] itemAttachmentData);

    /**
     * Sets (as xml) the "ItemAttachmentData" element
     */
    void xsetItemAttachmentData(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType itemAttachmentData);

    /**
     * Unsets the "ItemAttachmentData" element
     */
    void unsetItemAttachmentData();

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
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDescriptionType xgetDescription();

    /**
     * Sets the "description" attribute
     */
    void setDescription(java.lang.String description);

    /**
     * Sets (as xml) the "description" attribute
     */
    void xsetDescription(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDescriptionType description);

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
