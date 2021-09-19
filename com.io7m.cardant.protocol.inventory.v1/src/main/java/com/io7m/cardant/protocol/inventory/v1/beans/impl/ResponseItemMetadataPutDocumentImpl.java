/*
 * An XML document type.
 * Localname: ResponseItemMetadataPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseItemMetadataPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemMetadataPutDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutDocument {
    private static final long serialVersionUID = 1L;

    public ResponseItemMetadataPutDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemMetadataPut"),
    };


    /**
     * Gets the "ResponseItemMetadataPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutType getResponseItemMetadataPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseItemMetadataPut" element
     */
    @Override
    public void setResponseItemMetadataPut(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutType responseItemMetadataPut) {
        generatedSetterHelperImpl(responseItemMetadataPut, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseItemMetadataPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutType addNewResponseItemMetadataPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
