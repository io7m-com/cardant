/*
 * An XML document type.
 * Localname: CommandItemMetadataRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandItemMetadataRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemMetadataRemoveDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveDocument {
    private static final long serialVersionUID = 1L;

    public CommandItemMetadataRemoveDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemMetadataRemove"),
    };


    /**
     * Gets the "CommandItemMetadataRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType getCommandItemMetadataRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandItemMetadataRemove" element
     */
    @Override
    public void setCommandItemMetadataRemove(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType commandItemMetadataRemove) {
        generatedSetterHelperImpl(commandItemMetadataRemove, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandItemMetadataRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType addNewCommandItemMetadataRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
