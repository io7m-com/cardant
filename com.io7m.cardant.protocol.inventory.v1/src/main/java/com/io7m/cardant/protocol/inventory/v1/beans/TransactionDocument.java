/*
 * An XML document type.
 * Localname: Transaction
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TransactionDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Transaction(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface TransactionDocument extends com.io7m.cardant.protocol.inventory.v1.beans.MessageDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.TransactionDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "transaction823bdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Transaction" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.TransactionType getTransaction();

    /**
     * Sets the "Transaction" element
     */
    void setTransaction(com.io7m.cardant.protocol.inventory.v1.beans.TransactionType transaction);

    /**
     * Appends and returns a new empty "Transaction" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.TransactionType addNewTransaction();
}
