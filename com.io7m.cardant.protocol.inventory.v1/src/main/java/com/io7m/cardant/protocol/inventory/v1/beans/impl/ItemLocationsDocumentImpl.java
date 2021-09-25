/*
 * An XML document type.
 * Localname: ItemLocations
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ItemLocations(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemLocationsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsDocument {
    private static final long serialVersionUID = 1L;

    public ItemLocationsDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemLocations"),
    };


    /**
     * Gets the "ItemLocations" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType getItemLocations() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemLocations" element
     */
    @Override
    public void setItemLocations(com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType itemLocations) {
        generatedSetterHelperImpl(itemLocations, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ItemLocations" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType addNewItemLocations() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
