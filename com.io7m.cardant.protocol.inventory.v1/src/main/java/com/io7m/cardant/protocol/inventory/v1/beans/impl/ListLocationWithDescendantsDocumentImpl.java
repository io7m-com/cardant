/*
 * An XML document type.
 * Localname: ListLocationWithDescendants
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ListLocationWithDescendants(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ListLocationWithDescendantsDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ListLocationsBehaviourDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsDocument {
    private static final long serialVersionUID = 1L;

    public ListLocationWithDescendantsDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ListLocationWithDescendants"),
    };


    /**
     * Gets the "ListLocationWithDescendants" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType getListLocationWithDescendants() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ListLocationWithDescendants" element
     */
    @Override
    public void setListLocationWithDescendants(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType listLocationWithDescendants) {
        generatedSetterHelperImpl(listLocationWithDescendants, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ListLocationWithDescendants" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType addNewListLocationWithDescendants() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
