/*
 * An XML document type.
 * Localname: CommandLoginUsernamePassword
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandLoginUsernamePassword(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandLoginUsernamePasswordDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordDocument {
    private static final long serialVersionUID = 1L;

    public CommandLoginUsernamePasswordDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLoginUsernamePassword"),
    };


    /**
     * Gets the "CommandLoginUsernamePassword" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType getCommandLoginUsernamePassword() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandLoginUsernamePassword" element
     */
    @Override
    public void setCommandLoginUsernamePassword(com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType commandLoginUsernamePassword) {
        generatedSetterHelperImpl(commandLoginUsernamePassword, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandLoginUsernamePassword" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType addNewCommandLoginUsernamePassword() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
