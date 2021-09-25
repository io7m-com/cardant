/*
 * An XML document type.
 * Localname: ResponseItemLocationsList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseItemLocationsList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemLocationsListDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListDocument {
    private static final long serialVersionUID = 1L;

    public ResponseItemLocationsListDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemLocationsList"),
    };


    /**
     * Gets the "ResponseItemLocationsList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListType getResponseItemLocationsList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseItemLocationsList" element
     */
    @Override
    public void setResponseItemLocationsList(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListType responseItemLocationsList) {
        generatedSetterHelperImpl(responseItemLocationsList, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseItemLocationsList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListType addNewResponseItemLocationsList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
