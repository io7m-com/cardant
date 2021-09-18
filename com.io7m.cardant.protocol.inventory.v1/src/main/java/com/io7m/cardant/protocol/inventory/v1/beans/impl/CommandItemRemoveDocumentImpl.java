/*
 * An XML document type.
 * Localname: CommandItemRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandItemRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemRemoveDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveDocument {
    private static final long serialVersionUID = 1L;

    public CommandItemRemoveDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemRemove"),
    };


    /**
     * Gets the "CommandItemRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveType getCommandItemRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandItemRemove" element
     */
    @Override
    public void setCommandItemRemove(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveType commandItemRemove) {
        generatedSetterHelperImpl(commandItemRemove, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandItemRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveType addNewCommandItemRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
