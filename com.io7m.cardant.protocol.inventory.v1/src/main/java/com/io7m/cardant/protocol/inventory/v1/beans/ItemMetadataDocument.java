/*
 * An XML document type.
 * Localname: ItemMetadata
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemMetadata(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemMetadataDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "itemmetadata2b25doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemMetadata" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType getItemMetadata();

    /**
     * Sets the "ItemMetadata" element
     */
    void setItemMetadata(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType itemMetadata);

    /**
     * Appends and returns a new empty "ItemMetadata" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType addNewItemMetadata();
}
