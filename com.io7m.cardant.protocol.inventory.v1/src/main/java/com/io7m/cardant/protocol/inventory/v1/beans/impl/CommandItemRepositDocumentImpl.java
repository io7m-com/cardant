/*
 * An XML document type.
 * Localname: CommandItemReposit
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandItemReposit(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemRepositDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositDocument {
    private static final long serialVersionUID = 1L;

    public CommandItemRepositDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemReposit"),
    };


    /**
     * Gets the "CommandItemReposit" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType getCommandItemReposit() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandItemReposit" element
     */
    @Override
    public void setCommandItemReposit(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType commandItemReposit) {
        generatedSetterHelperImpl(commandItemReposit, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandItemReposit" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType addNewCommandItemReposit() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
