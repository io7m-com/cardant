/*
 * An XML document type.
 * Localname: CommandItemAttachmentPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandItemAttachmentPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemAttachmentPutDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutDocument {
    private static final long serialVersionUID = 1L;

    public CommandItemAttachmentPutDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemAttachmentPut"),
    };


    /**
     * Gets the "CommandItemAttachmentPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType getCommandItemAttachmentPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandItemAttachmentPut" element
     */
    @Override
    public void setCommandItemAttachmentPut(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType commandItemAttachmentPut) {
        generatedSetterHelperImpl(commandItemAttachmentPut, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandItemAttachmentPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType addNewCommandItemAttachmentPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
