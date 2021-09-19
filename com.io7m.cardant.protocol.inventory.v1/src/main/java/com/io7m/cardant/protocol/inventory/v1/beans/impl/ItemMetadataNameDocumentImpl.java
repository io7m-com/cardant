/*
 * An XML document type.
 * Localname: ItemMetadataName
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ItemMetadataName(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemMetadataNameDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameDocument {
    private static final long serialVersionUID = 1L;

    public ItemMetadataNameDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadataName"),
    };


    /**
     * Gets the "ItemMetadataName" element
     */
    @Override
    public java.lang.String getItemMetadataName() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "ItemMetadataName" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType xgetItemMetadataName() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * Sets the "ItemMetadataName" element
     */
    @Override
    public void setItemMetadataName(java.lang.String itemMetadataName) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(itemMetadataName);
        }
    }

    /**
     * Sets (as xml) the "ItemMetadataName" element
     */
    @Override
    public void xsetItemMetadataName(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType itemMetadataName) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(itemMetadataName);
        }
    }
}
