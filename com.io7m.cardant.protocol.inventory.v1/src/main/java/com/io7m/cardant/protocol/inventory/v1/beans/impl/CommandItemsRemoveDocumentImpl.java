/*
 * An XML document type.
 * Localname: CommandItemsRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandItemsRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemsRemoveDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveDocument {
    private static final long serialVersionUID = 1L;

    public CommandItemsRemoveDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemsRemove"),
    };


    /**
     * Gets the "CommandItemsRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveType getCommandItemsRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandItemsRemove" element
     */
    @Override
    public void setCommandItemsRemove(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveType commandItemsRemove) {
        generatedSetterHelperImpl(commandItemsRemove, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandItemsRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveType addNewCommandItemsRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
