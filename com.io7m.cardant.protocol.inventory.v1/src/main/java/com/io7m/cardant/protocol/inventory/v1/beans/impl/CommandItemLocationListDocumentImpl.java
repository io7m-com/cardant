/*
 * An XML document type.
 * Localname: CommandItemLocationList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandItemLocationList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemLocationListDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListDocument {
    private static final long serialVersionUID = 1L;

    public CommandItemLocationListDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemLocationList"),
    };


    /**
     * Gets the "CommandItemLocationList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListType getCommandItemLocationList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandItemLocationList" element
     */
    @Override
    public void setCommandItemLocationList(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListType commandItemLocationList) {
        generatedSetterHelperImpl(commandItemLocationList, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandItemLocationList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListType addNewCommandItemLocationList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
