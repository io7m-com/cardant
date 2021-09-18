/*
 * An XML document type.
 * Localname: CommandItemAttachmentRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandItemAttachmentRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemAttachmentRemoveDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveDocument {
    private static final long serialVersionUID = 1L;

    public CommandItemAttachmentRemoveDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemAttachmentRemove"),
    };


    /**
     * Gets the "CommandItemAttachmentRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType getCommandItemAttachmentRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandItemAttachmentRemove" element
     */
    @Override
    public void setCommandItemAttachmentRemove(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType commandItemAttachmentRemove) {
        generatedSetterHelperImpl(commandItemAttachmentRemove, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandItemAttachmentRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType addNewCommandItemAttachmentRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
