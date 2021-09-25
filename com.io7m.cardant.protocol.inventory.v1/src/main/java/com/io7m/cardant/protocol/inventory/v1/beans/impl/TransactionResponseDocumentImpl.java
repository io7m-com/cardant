/*
 * An XML document type.
 * Localname: TransactionResponse
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one TransactionResponse(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class TransactionResponseDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.MessageDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseDocument {
    private static final long serialVersionUID = 1L;

    public TransactionResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "TransactionResponse"),
    };


    /**
     * Gets the "TransactionResponse" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType getTransactionResponse() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "TransactionResponse" element
     */
    @Override
    public void setTransactionResponse(com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType transactionResponse) {
        generatedSetterHelperImpl(transactionResponse, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "TransactionResponse" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType addNewTransactionResponse() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
