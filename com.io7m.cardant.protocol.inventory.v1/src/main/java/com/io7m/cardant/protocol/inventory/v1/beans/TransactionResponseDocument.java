/*
 * An XML document type.
 * Localname: TransactionResponse
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one TransactionResponse(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface TransactionResponseDocument extends com.io7m.cardant.protocol.inventory.v1.beans.MessageDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "transactionresponse2efadoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "TransactionResponse" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType getTransactionResponse();

    /**
     * Sets the "TransactionResponse" element
     */
    void setTransactionResponse(com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType transactionResponse);

    /**
     * Appends and returns a new empty "TransactionResponse" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType addNewTransactionResponse();
}
