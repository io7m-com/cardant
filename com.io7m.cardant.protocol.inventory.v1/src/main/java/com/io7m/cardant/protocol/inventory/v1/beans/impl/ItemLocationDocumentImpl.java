/*
 * An XML document type.
 * Localname: ItemLocation
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ItemLocation(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemLocationDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationDocument {
    private static final long serialVersionUID = 1L;

    public ItemLocationDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemLocation"),
    };


    /**
     * Gets the "ItemLocation" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType getItemLocation() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemLocation" element
     */
    @Override
    public void setItemLocation(com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType itemLocation) {
        generatedSetterHelperImpl(itemLocation, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ItemLocation" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType addNewItemLocation() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
