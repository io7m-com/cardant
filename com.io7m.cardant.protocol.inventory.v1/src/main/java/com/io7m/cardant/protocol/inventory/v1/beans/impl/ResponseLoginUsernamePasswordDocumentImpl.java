/*
 * An XML document type.
 * Localname: ResponseLoginUsernamePassword
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseLoginUsernamePassword(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseLoginUsernamePasswordDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordDocument {
    private static final long serialVersionUID = 1L;

    public ResponseLoginUsernamePasswordDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseLoginUsernamePassword"),
    };


    /**
     * Gets the "ResponseLoginUsernamePassword" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordType getResponseLoginUsernamePassword() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseLoginUsernamePassword" element
     */
    @Override
    public void setResponseLoginUsernamePassword(com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordType responseLoginUsernamePassword) {
        generatedSetterHelperImpl(responseLoginUsernamePassword, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseLoginUsernamePassword" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordType addNewResponseLoginUsernamePassword() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
