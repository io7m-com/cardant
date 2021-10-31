/*
 * An XML document type.
 * Localname: CommandFileRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandFileRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandFileRemoveDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveDocument {
    private static final long serialVersionUID = 1L;

    public CommandFileRemoveDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandFileRemove"),
    };


    /**
     * Gets the "CommandFileRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveType getCommandFileRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandFileRemove" element
     */
    @Override
    public void setCommandFileRemove(com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveType commandFileRemove) {
        generatedSetterHelperImpl(commandFileRemove, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandFileRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveType addNewCommandFileRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
