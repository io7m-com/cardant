/*
 * An XML document type.
 * Localname: ItemAttachmentData
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ItemAttachmentData(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemAttachmentDataDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataDocument {
    private static final long serialVersionUID = 1L;

    public ItemAttachmentDataDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachmentData"),
    };


    /**
     * Gets the "ItemAttachmentData" element
     */
    @Override
    public byte[] getItemAttachmentData() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getByteArrayValue();
        }
    }

    /**
     * Gets (as xml) the "ItemAttachmentData" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType xgetItemAttachmentData() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * Sets the "ItemAttachmentData" element
     */
    @Override
    public void setItemAttachmentData(byte[] itemAttachmentData) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setByteArrayValue(itemAttachmentData);
        }
    }

    /**
     * Sets (as xml) the "ItemAttachmentData" element
     */
    @Override
    public void xsetItemAttachmentData(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType itemAttachmentData) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(itemAttachmentData);
        }
    }
}
