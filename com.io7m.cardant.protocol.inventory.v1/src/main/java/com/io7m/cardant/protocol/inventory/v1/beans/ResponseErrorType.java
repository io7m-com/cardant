/*
 * XML Type:  ResponseErrorType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

import java.math.BigInteger;
import java.util.List;


/**
 * An XML ResponseErrorType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseErrorType extends ResponseType
{
  DocumentFactory<ResponseErrorType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responseerrortype6288type");
  SchemaType type = Factory.getType();


  /**
   * Gets a List of "ResponseErrorDetail" elements
   */
  List<ResponseErrorDetailType> getResponseErrorDetailList();

  /**
   * Gets array of all "ResponseErrorDetail" elements
   */
  ResponseErrorDetailType[] getResponseErrorDetailArray();

  /**
   * Sets array of all "ResponseErrorDetail" element
   */
  void setResponseErrorDetailArray(ResponseErrorDetailType[] responseErrorDetailArray);

  /**
   * Gets ith "ResponseErrorDetail" element
   */
  ResponseErrorDetailType getResponseErrorDetailArray(int i);

  /**
   * Returns number of "ResponseErrorDetail" element
   */
  int sizeOfResponseErrorDetailArray();

  /**
   * Sets ith "ResponseErrorDetail" element
   */
  void setResponseErrorDetailArray(
    int i,
    ResponseErrorDetailType responseErrorDetail);

  /**
   * Inserts and returns a new empty value (as xml) as the ith "ResponseErrorDetail" element
   */
  ResponseErrorDetailType insertNewResponseErrorDetail(int i);

  /**
   * Appends and returns a new empty value (as xml) as the last "ResponseErrorDetail" element
   */
  ResponseErrorDetailType addNewResponseErrorDetail();

  /**
   * Removes the ith "ResponseErrorDetail" element
   */
  void removeResponseErrorDetail(int i);

  /**
   * Gets the "status" attribute
   */
  BigInteger getStatus();

  /**
   * Sets the "status" attribute
   */
  void setStatus(BigInteger status);

  /**
   * Gets (as xml) the "status" attribute
   */
  XmlInteger xgetStatus();

  /**
   * Sets (as xml) the "status" attribute
   */
  void xsetStatus(XmlInteger status);

  /**
   * Gets the "message" attribute
   */
  String getMessage();

  /**
   * Sets the "message" attribute
   */
  void setMessage(String message);

  /**
   * Gets (as xml) the "message" attribute
   */
  XmlString xgetMessage();

  /**
   * Sets (as xml) the "message" attribute
   */
  void xsetMessage(XmlString message);
}
