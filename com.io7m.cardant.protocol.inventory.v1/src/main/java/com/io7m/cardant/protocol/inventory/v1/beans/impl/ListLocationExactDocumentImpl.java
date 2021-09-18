/*
 * An XML document type.
 * Localname: ListLocationExact
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ListLocationExact(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ListLocationExactDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ListLocationsBehaviourDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactDocument {
    private static final long serialVersionUID = 1L;

    public ListLocationExactDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ListLocationExact"),
    };


    /**
     * Gets the "ListLocationExact" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType getListLocationExact() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ListLocationExact" element
     */
    @Override
    public void setListLocationExact(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType listLocationExact) {
        generatedSetterHelperImpl(listLocationExact, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ListLocationExact" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType addNewListLocationExact() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
