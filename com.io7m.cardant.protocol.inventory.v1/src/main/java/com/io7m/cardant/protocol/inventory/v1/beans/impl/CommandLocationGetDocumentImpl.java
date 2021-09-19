/*
 * An XML document type.
 * Localname: CommandLocationGet
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandLocationGet(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandLocationGetDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetDocument {
    private static final long serialVersionUID = 1L;

    public CommandLocationGetDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationGet"),
    };


    /**
     * Gets the "CommandLocationGet" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetType getCommandLocationGet() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandLocationGet" element
     */
    @Override
    public void setCommandLocationGet(com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetType commandLocationGet) {
        generatedSetterHelperImpl(commandLocationGet, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandLocationGet" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetType addNewCommandLocationGet() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
