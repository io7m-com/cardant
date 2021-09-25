/*
 * An XML document type.
 * Localname: CommandItemLocationsList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandItemLocationsList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemLocationsListDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListDocument {
    private static final long serialVersionUID = 1L;

    public CommandItemLocationsListDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemLocationsList"),
    };


    /**
     * Gets the "CommandItemLocationsList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListType getCommandItemLocationsList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandItemLocationsList" element
     */
    @Override
    public void setCommandItemLocationsList(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListType commandItemLocationsList) {
        generatedSetterHelperImpl(commandItemLocationsList, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandItemLocationsList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListType addNewCommandItemLocationsList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
