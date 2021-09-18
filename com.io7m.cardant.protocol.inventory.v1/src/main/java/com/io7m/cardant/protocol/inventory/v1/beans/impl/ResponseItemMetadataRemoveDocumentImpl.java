/*
 * An XML document type.
 * Localname: ResponseItemMetadataRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseItemMetadataRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemMetadataRemoveDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveDocument {
    private static final long serialVersionUID = 1L;

    public ResponseItemMetadataRemoveDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemMetadataRemove"),
    };


    /**
     * Gets the "ResponseItemMetadataRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveType getResponseItemMetadataRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseItemMetadataRemove" element
     */
    @Override
    public void setResponseItemMetadataRemove(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveType responseItemMetadataRemove) {
        generatedSetterHelperImpl(responseItemMetadataRemove, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseItemMetadataRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveType addNewResponseItemMetadataRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
