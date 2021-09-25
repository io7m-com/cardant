/*
 * XML Type:  TransactionResponseType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML TransactionResponseType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface TransactionResponseType extends com.io7m.cardant.protocol.inventory.v1.beans.MessageType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "transactionresponsetype03d0type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "Response" elements
     */
    java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ResponseType> getResponseList();

    /**
     * Gets array of all "Response" elements
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseType[] getResponseArray();

    /**
     * Gets ith "Response" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseType getResponseArray(int i);

    /**
     * Returns number of "Response" element
     */
    int sizeOfResponseArray();

    /**
     * Sets array of all "Response" element
     */
    void setResponseArray(com.io7m.cardant.protocol.inventory.v1.beans.ResponseType[] responseArray);

    /**
     * Sets ith "Response" element
     */
    void setResponseArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ResponseType response);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Response" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseType insertNewResponse(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "Response" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseType addNewResponse();

    /**
     * Removes the ith "Response" element
     */
    void removeResponse(int i);

    /**
     * Gets the "failed" attribute
     */
    boolean getFailed();

    /**
     * Gets (as xml) the "failed" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetFailed();

    /**
     * Sets the "failed" attribute
     */
    void setFailed(boolean failed);

    /**
     * Sets (as xml) the "failed" attribute
     */
    void xsetFailed(org.apache.xmlbeans.XmlBoolean failed);
}
