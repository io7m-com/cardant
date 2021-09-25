/*
 * An XML document type.
 * Localname: ItemRepositMove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ItemRepositMove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemRepositMoveDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ItemRepositDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveDocument {
    private static final long serialVersionUID = 1L;

    public ItemRepositMoveDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemRepositMove"),
    };


    /**
     * Gets the "ItemRepositMove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType getItemRepositMove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemRepositMove" element
     */
    @Override
    public void setItemRepositMove(com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType itemRepositMove) {
        generatedSetterHelperImpl(itemRepositMove, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ItemRepositMove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType addNewItemRepositMove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
