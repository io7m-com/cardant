/*
 * An XML document type.
 * Localname: CommandFilePut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandFilePut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandFilePutDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutDocument {
    private static final long serialVersionUID = 1L;

    public CommandFilePutDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandFilePut"),
    };


    /**
     * Gets the "CommandFilePut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutType getCommandFilePut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandFilePut" element
     */
    @Override
    public void setCommandFilePut(com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutType commandFilePut) {
        generatedSetterHelperImpl(commandFilePut, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandFilePut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutType addNewCommandFilePut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
