/*
 * XML Type:  ResponseErrorType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ResponseErrorType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseErrorType extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "responseerrortype6288type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "ResponseErrorDetail" elements
     */
    java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType> getResponseErrorDetailList();

    /**
     * Gets array of all "ResponseErrorDetail" elements
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType[] getResponseErrorDetailArray();

    /**
     * Gets ith "ResponseErrorDetail" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType getResponseErrorDetailArray(int i);

    /**
     * Returns number of "ResponseErrorDetail" element
     */
    int sizeOfResponseErrorDetailArray();

    /**
     * Sets array of all "ResponseErrorDetail" element
     */
    void setResponseErrorDetailArray(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType[] responseErrorDetailArray);

    /**
     * Sets ith "ResponseErrorDetail" element
     */
    void setResponseErrorDetailArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType responseErrorDetail);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ResponseErrorDetail" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType insertNewResponseErrorDetail(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "ResponseErrorDetail" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType addNewResponseErrorDetail();

    /**
     * Removes the ith "ResponseErrorDetail" element
     */
    void removeResponseErrorDetail(int i);

    /**
     * Gets the "status" attribute
     */
    java.math.BigInteger getStatus();

    /**
     * Gets (as xml) the "status" attribute
     */
    org.apache.xmlbeans.XmlInteger xgetStatus();

    /**
     * Sets the "status" attribute
     */
    void setStatus(java.math.BigInteger status);

    /**
     * Sets (as xml) the "status" attribute
     */
    void xsetStatus(org.apache.xmlbeans.XmlInteger status);

    /**
     * Gets the "message" attribute
     */
    java.lang.String getMessage();

    /**
     * Gets (as xml) the "message" attribute
     */
    org.apache.xmlbeans.XmlString xgetMessage();

    /**
     * Sets the "message" attribute
     */
    void setMessage(java.lang.String message);

    /**
     * Sets (as xml) the "message" attribute
     */
    void xsetMessage(org.apache.xmlbeans.XmlString message);
}
