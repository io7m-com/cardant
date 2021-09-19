/*
 * An XML document type.
 * Localname: ListLocationsBehaviour
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ListLocationsBehaviour(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ListLocationsBehaviourDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourDocument {
    private static final long serialVersionUID = 1L;

    public ListLocationsBehaviourDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ListLocationsBehaviour"),
    };

    private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray( new QName[] { 
        new QName("urn:com.io7m.cardant.inventory:1", "ListLocationsAll"),
        new QName("urn:com.io7m.cardant.inventory:1", "ListLocationWithDescendants"),
        new QName("urn:com.io7m.cardant.inventory:1", "ListLocationsBehaviour"),
        new QName("urn:com.io7m.cardant.inventory:1", "ListLocationExact"),
    }),
    };

    /**
     * Gets the "ListLocationsBehaviour" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType getListLocationsBehaviour() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ListLocationsBehaviour" element
     */
    @Override
    public void setListLocationsBehaviour(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType listLocationsBehaviour) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(listLocationsBehaviour);
        }
    }

    /**
     * Appends and returns a new empty "ListLocationsBehaviour" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType addNewListLocationsBehaviour() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
