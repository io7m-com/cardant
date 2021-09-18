/*
 * An XML document type.
 * Localname: CommandItemUpdate
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandItemUpdate(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemUpdateDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateDocument {
    private static final long serialVersionUID = 1L;

    public CommandItemUpdateDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemUpdate"),
    };


    /**
     * Gets the "CommandItemUpdate" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateType getCommandItemUpdate() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandItemUpdate" element
     */
    @Override
    public void setCommandItemUpdate(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateType commandItemUpdate) {
        generatedSetterHelperImpl(commandItemUpdate, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandItemUpdate" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateType addNewCommandItemUpdate() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
