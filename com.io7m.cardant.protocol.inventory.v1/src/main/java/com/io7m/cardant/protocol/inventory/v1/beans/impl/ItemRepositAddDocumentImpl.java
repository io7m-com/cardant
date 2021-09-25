/*
 * An XML document type.
 * Localname: ItemRepositAdd
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ItemRepositAdd(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemRepositAddDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ItemRepositDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddDocument {
    private static final long serialVersionUID = 1L;

    public ItemRepositAddDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemRepositAdd"),
    };


    /**
     * Gets the "ItemRepositAdd" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddType getItemRepositAdd() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemRepositAdd" element
     */
    @Override
    public void setItemRepositAdd(com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddType itemRepositAdd) {
        generatedSetterHelperImpl(itemRepositAdd, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ItemRepositAdd" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddType addNewItemRepositAdd() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
