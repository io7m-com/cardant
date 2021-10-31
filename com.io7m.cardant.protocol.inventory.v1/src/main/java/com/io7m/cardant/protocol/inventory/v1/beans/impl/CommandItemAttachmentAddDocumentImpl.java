/*
 * An XML document type.
 * Localname: CommandItemAttachmentAdd
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandItemAttachmentAdd(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemAttachmentAddDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddDocument {
    private static final long serialVersionUID = 1L;

    public CommandItemAttachmentAddDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemAttachmentAdd"),
    };


    /**
     * Gets the "CommandItemAttachmentAdd" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddType getCommandItemAttachmentAdd() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandItemAttachmentAdd" element
     */
    @Override
    public void setCommandItemAttachmentAdd(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddType commandItemAttachmentAdd) {
        generatedSetterHelperImpl(commandItemAttachmentAdd, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandItemAttachmentAdd" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddType addNewCommandItemAttachmentAdd() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
