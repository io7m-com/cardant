/*
 * XML Type:  ResponseItemGetType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ResponseItemGetType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ResponseItemGetTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType {
    private static final long serialVersionUID = 1L;

    public ResponseItemGetTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Item"),
    };


    /**
     * Gets the "Item" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemType getItem() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Item" element
     */
    @Override
    public void setItem(com.io7m.cardant.protocol.inventory.v1.beans.ItemType item) {
        generatedSetterHelperImpl(item, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "Item" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemType addNewItem() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
