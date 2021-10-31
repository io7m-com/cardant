/*
 * An XML document type.
 * Localname: ID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.IDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class IDDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.IDDocument {
    private static final long serialVersionUID = 1L;

    public IDDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ID"),
    };

    private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray( new QName[] { 
        new QName("urn:com.io7m.cardant.inventory:1", "ItemID"),
        new QName("urn:com.io7m.cardant.inventory:1", "TagID"),
        new QName("urn:com.io7m.cardant.inventory:1", "LocationID"),
        new QName("urn:com.io7m.cardant.inventory:1", "ID"),
        new QName("urn:com.io7m.cardant.inventory:1", "FileID"),
        new QName("urn:com.io7m.cardant.inventory:1", "UserID"),
    }),
    };

    /**
     * Gets the "ID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.IDType getID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.IDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.IDType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ID" element
     */
    @Override
    public void setID(com.io7m.cardant.protocol.inventory.v1.beans.IDType id) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.IDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.IDType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.IDType)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(id);
        }
    }

    /**
     * Appends and returns a new empty "ID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.IDType addNewID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.IDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.IDType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
