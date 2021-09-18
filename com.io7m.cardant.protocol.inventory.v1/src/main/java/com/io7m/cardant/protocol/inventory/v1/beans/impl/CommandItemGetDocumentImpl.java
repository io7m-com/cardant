/*
 * An XML document type.
 * Localname: CommandItemGet
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandItemGet(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemGetDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetDocument {
    private static final long serialVersionUID = 1L;

    public CommandItemGetDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemGet"),
    };


    /**
     * Gets the "CommandItemGet" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetType getCommandItemGet() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandItemGet" element
     */
    @Override
    public void setCommandItemGet(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetType commandItemGet) {
        generatedSetterHelperImpl(commandItemGet, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandItemGet" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetType addNewCommandItemGet() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
