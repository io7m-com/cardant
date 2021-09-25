/*
 * XML Type:  ResponseErrorDetailsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ResponseErrorDetailsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseErrorDetailsType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "responseerrordetailstypedaa2type");
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
}
