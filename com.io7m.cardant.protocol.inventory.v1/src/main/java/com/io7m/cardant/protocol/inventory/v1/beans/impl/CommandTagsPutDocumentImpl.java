/*
 * An XML document type.
 * Localname: CommandTagsPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandTagsPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandTagsPutDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutDocument {
    private static final long serialVersionUID = 1L;

    public CommandTagsPutDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsPut"),
    };


    /**
     * Gets the "CommandTagsPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutType getCommandTagsPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandTagsPut" element
     */
    @Override
    public void setCommandTagsPut(com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutType commandTagsPut) {
        generatedSetterHelperImpl(commandTagsPut, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandTagsPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutType addNewCommandTagsPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
