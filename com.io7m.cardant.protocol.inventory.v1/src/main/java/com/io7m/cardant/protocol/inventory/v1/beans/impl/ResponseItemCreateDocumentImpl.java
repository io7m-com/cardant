/*
 * An XML document type.
 * Localname: ResponseItemCreate
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseItemCreate(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemCreateDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateDocument {
    private static final long serialVersionUID = 1L;

    public ResponseItemCreateDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemCreate"),
    };


    /**
     * Gets the "ResponseItemCreate" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType getResponseItemCreate() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseItemCreate" element
     */
    @Override
    public void setResponseItemCreate(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType responseItemCreate) {
        generatedSetterHelperImpl(responseItemCreate, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseItemCreate" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType addNewResponseItemCreate() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
