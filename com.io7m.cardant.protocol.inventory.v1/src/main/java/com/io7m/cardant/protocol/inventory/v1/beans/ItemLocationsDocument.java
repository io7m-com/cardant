/*
 * An XML document type.
 * Localname: ItemLocations
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemLocations(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemLocationsDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "itemlocations7aeedoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemLocations" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType getItemLocations();

    /**
     * Sets the "ItemLocations" element
     */
    void setItemLocations(com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType itemLocations);

    /**
     * Appends and returns a new empty "ItemLocations" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType addNewItemLocations();
}
