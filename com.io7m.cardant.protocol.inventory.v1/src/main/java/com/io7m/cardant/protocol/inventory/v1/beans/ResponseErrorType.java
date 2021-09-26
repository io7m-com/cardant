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
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "responseerrortype6288type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseErrorAttributes" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType getResponseErrorAttributes();

    /**
     * Sets the "ResponseErrorAttributes" element
     */
    void setResponseErrorAttributes(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType responseErrorAttributes);

    /**
     * Appends and returns a new empty "ResponseErrorAttributes" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType addNewResponseErrorAttributes();

    /**
     * Gets the "ResponseErrorDetails" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType getResponseErrorDetails();

    /**
     * Sets the "ResponseErrorDetails" element
     */
    void setResponseErrorDetails(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType responseErrorDetails);

    /**
     * Appends and returns a new empty "ResponseErrorDetails" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType addNewResponseErrorDetails();

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
     * Gets the "summary" attribute
     */
    java.lang.String getSummary();

    /**
     * Gets (as xml) the "summary" attribute
     */
    org.apache.xmlbeans.XmlString xgetSummary();

    /**
     * Sets the "summary" attribute
     */
    void setSummary(java.lang.String summary);

    /**
     * Sets (as xml) the "summary" attribute
     */
    void xsetSummary(org.apache.xmlbeans.XmlString summary);
}
