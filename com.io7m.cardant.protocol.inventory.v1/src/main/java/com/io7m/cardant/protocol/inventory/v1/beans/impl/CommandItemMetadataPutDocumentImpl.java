/*
 * An XML document type.
 * Localname: CommandItemMetadataPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandItemMetadataPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemMetadataPutDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutDocument {
    private static final long serialVersionUID = 1L;

    public CommandItemMetadataPutDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemMetadataPut"),
    };


    /**
     * Gets the "CommandItemMetadataPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType getCommandItemMetadataPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandItemMetadataPut" element
     */
    @Override
    public void setCommandItemMetadataPut(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType commandItemMetadataPut) {
        generatedSetterHelperImpl(commandItemMetadataPut, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandItemMetadataPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType addNewCommandItemMetadataPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
