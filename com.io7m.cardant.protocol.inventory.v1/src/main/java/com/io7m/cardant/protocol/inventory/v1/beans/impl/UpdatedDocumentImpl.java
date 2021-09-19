/*
 * An XML document type.
 * Localname: Updated
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.UpdatedDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one Updated(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class UpdatedDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.UpdatedDocument {
    private static final long serialVersionUID = 1L;

    public UpdatedDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Updated"),
    };


    /**
     * Gets the "Updated" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.UpdatedType getUpdated() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UpdatedType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UpdatedType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Updated" element
     */
    @Override
    public void setUpdated(com.io7m.cardant.protocol.inventory.v1.beans.UpdatedType updated) {
        generatedSetterHelperImpl(updated, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "Updated" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.UpdatedType addNewUpdated() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UpdatedType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UpdatedType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
