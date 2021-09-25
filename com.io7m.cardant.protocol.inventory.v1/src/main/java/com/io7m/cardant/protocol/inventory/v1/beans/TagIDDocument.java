/*
 * An XML document type.
 * Localname: TagID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TagIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one TagID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface TagIDDocument extends com.io7m.cardant.protocol.inventory.v1.beans.IDDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.TagIDDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "tagid1884doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "TagID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.TagIDType getTagID();

    /**
     * Sets the "TagID" element
     */
    void setTagID(com.io7m.cardant.protocol.inventory.v1.beans.TagIDType tagID);

    /**
     * Appends and returns a new empty "TagID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.TagIDType addNewTagID();
}
