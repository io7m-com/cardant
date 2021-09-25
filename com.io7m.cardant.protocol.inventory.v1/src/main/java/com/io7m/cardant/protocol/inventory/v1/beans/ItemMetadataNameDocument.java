/*
 * An XML document type.
 * Localname: ItemMetadataName
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemMetadataName(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemMetadataNameDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "itemmetadataname035adoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemMetadataName" element
     */
    java.lang.String getItemMetadataName();

    /**
     * Gets (as xml) the "ItemMetadataName" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType xgetItemMetadataName();

    /**
     * Sets the "ItemMetadataName" element
     */
    void setItemMetadataName(java.lang.String itemMetadataName);

    /**
     * Sets (as xml) the "ItemMetadataName" element
     */
    void xsetItemMetadataName(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType itemMetadataName);
}
