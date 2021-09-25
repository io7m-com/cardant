/*
 * An XML document type.
 * Localname: CommandLocationPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandLocationPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandLocationPutDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutDocument {
    private static final long serialVersionUID = 1L;

    public CommandLocationPutDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationPut"),
    };


    /**
     * Gets the "CommandLocationPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType getCommandLocationPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandLocationPut" element
     */
    @Override
    public void setCommandLocationPut(com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType commandLocationPut) {
        generatedSetterHelperImpl(commandLocationPut, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandLocationPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType addNewCommandLocationPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
