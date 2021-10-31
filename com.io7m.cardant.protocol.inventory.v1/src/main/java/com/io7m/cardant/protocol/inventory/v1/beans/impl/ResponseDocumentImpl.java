/*
 * An XML document type.
 * Localname: Response
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one Response(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.MessageDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    private static final long serialVersionUID = 1L;

    public ResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Response"),
    };

    private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray( new QName[] { 
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagList"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemList"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemsRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsDelete"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseFilePut"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemMetadataRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemMetadataPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "Response"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseLoginUsernamePassword"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseFileRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemCreate"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemAttachmentRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemLocationsList"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseLocationList"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemUpdate"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseError"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemGet"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseLocationPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemAttachmentAdd"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemReposit"),
    }),
    };

    /**
     * Gets the "Response" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseType getResponse() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Response" element
     */
    @Override
    public void setResponse(com.io7m.cardant.protocol.inventory.v1.beans.ResponseType response) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseType)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(response);
        }
    }

    /**
     * Appends and returns a new empty "Response" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseType addNewResponse() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
