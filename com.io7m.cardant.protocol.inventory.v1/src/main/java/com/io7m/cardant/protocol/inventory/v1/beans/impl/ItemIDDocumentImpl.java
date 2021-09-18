/*
 * An XML document type.
 * Localname: ItemID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ItemID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemIDDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.IDDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemIDDocument {
    private static final long serialVersionUID = 1L;

    public ItemIDDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemID"),
    };


    /**
     * Gets the "ItemID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType getItemID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemID" element
     */
    @Override
    public void setItemID(com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType itemID) {
        generatedSetterHelperImpl(itemID, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ItemID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType addNewItemID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
