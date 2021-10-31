/*
 * An XML document type.
 * Localname: ResponseItemMetadataPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseItemMetadataPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemMetadataPutDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "responseitemmetadataput438ddoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseItemMetadataPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutType getResponseItemMetadataPut();

    /**
     * Sets the "ResponseItemMetadataPut" element
     */
    void setResponseItemMetadataPut(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutType responseItemMetadataPut);

    /**
     * Appends and returns a new empty "ResponseItemMetadataPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutType addNewResponseItemMetadataPut();
}
