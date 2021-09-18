/*
 * An XML document type.
 * Localname: Removed
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.RemovedDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one Removed(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class RemovedDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.RemovedDocument {
    private static final long serialVersionUID = 1L;

    public RemovedDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Removed"),
    };


    /**
     * Gets the "Removed" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.RemovedType getRemoved() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.RemovedType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.RemovedType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Removed" element
     */
    @Override
    public void setRemoved(com.io7m.cardant.protocol.inventory.v1.beans.RemovedType removed) {
        generatedSetterHelperImpl(removed, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "Removed" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.RemovedType addNewRemoved() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.RemovedType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.RemovedType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
