/*
 * An XML document type.
 * Localname: ListLocationsAll
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ListLocationsAll(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ListLocationsAllDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ListLocationsBehaviourDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllDocument {
    private static final long serialVersionUID = 1L;

    public ListLocationsAllDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ListLocationsAll"),
    };


    /**
     * Gets the "ListLocationsAll" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType getListLocationsAll() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ListLocationsAll" element
     */
    @Override
    public void setListLocationsAll(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType listLocationsAll) {
        generatedSetterHelperImpl(listLocationsAll, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ListLocationsAll" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType addNewListLocationsAll() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
