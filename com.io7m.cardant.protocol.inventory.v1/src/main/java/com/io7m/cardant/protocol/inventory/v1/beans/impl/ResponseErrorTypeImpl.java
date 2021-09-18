/*
 * XML Type:  ResponseErrorType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.util.List;

/**
 * An XML ResponseErrorType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ResponseErrorTypeImpl extends ResponseTypeImpl implements
  ResponseErrorType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ResponseErrorDetail"),
    new QName("", "status"),
    new QName("", "message"),
  };

  public ResponseErrorTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets a List of "ResponseErrorDetail" elements
   */
  @Override
  public List<ResponseErrorDetailType> getResponseErrorDetailList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return new JavaListXmlObject<>(
        this::getResponseErrorDetailArray,
        this::setResponseErrorDetailArray,
        this::insertNewResponseErrorDetail,
        this::removeResponseErrorDetail,
        this::sizeOfResponseErrorDetailArray
      );
    }
  }

  /**
   * Gets array of all "ResponseErrorDetail" elements
   */
  @Override
  public ResponseErrorDetailType[] getResponseErrorDetailArray()
  {
    return this.getXmlObjectArray(
      PROPERTY_QNAME[0],
      new ResponseErrorDetailType[0]);
  }

  /**
   * Sets array of all "ResponseErrorDetail" element  WARNING: This method is not atomicaly synchronized.
   */
  @Override
  public void setResponseErrorDetailArray(final ResponseErrorDetailType[] responseErrorDetailArray)
  {
    this.check_orphaned();
    this.arraySetterHelper(responseErrorDetailArray, PROPERTY_QNAME[0]);
  }

  /**
   * Gets ith "ResponseErrorDetail" element
   */
  @Override
  public ResponseErrorDetailType getResponseErrorDetailArray(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseErrorDetailType target = null;
      target = (ResponseErrorDetailType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        i);
      if (target == null) {
        throw new IndexOutOfBoundsException();
      }
      return target;
    }
  }

  /**
   * Returns number of "ResponseErrorDetail" element
   */
  @Override
  public int sizeOfResponseErrorDetailArray()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QNAME[0]);
    }
  }

  /**
   * Sets ith "ResponseErrorDetail" element
   */
  @Override
  public void setResponseErrorDetailArray(
    final int i,
    final ResponseErrorDetailType responseErrorDetail)
  {
    this.generatedSetterHelperImpl(
      responseErrorDetail,
      PROPERTY_QNAME[0],
      i,
      XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
  }

  /**
   * Inserts and returns a new empty value (as xml) as the ith "ResponseErrorDetail" element
   */
  @Override
  public ResponseErrorDetailType insertNewResponseErrorDetail(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseErrorDetailType target = null;
      target = (ResponseErrorDetailType) this.get_store().insert_element_user(
        PROPERTY_QNAME[0],
        i);
      return target;
    }
  }

  /**
   * Appends and returns a new empty value (as xml) as the last "ResponseErrorDetail" element
   */
  @Override
  public ResponseErrorDetailType addNewResponseErrorDetail()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseErrorDetailType target = null;
      target = (ResponseErrorDetailType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Removes the ith "ResponseErrorDetail" element
   */
  @Override
  public void removeResponseErrorDetail(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QNAME[0], i);
    }
  }

  /**
   * Gets the "status" attribute
   */
  @Override
  public BigInteger getStatus()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      return (target == null) ? null : target.getBigIntegerValue();
    }
  }

  /**
   * Sets the "status" attribute
   */
  @Override
  public void setStatus(final BigInteger status)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[1]);
      }
      target.setBigIntegerValue(status);
    }
  }

  /**
   * Gets (as xml) the "status" attribute
   */
  @Override
  public XmlInteger xgetStatus()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlInteger target = null;
      target = (XmlInteger) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "status" attribute
   */
  @Override
  public void xsetStatus(final XmlInteger status)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlInteger target = null;
      target = (XmlInteger) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      if (target == null) {
        target = (XmlInteger) this.get_store().add_attribute_user(
          PROPERTY_QNAME[1]);
      }
      target.set(status);
    }
  }

  /**
   * Gets the "message" attribute
   */
  @Override
  public String getMessage()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      return (target == null) ? null : target.getStringValue();
    }
  }

  /**
   * Sets the "message" attribute
   */
  @Override
  public void setMessage(final String message)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[2]);
      }
      target.setStringValue(message);
    }
  }

  /**
   * Gets (as xml) the "message" attribute
   */
  @Override
  public XmlString xgetMessage()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlString target = null;
      target = (XmlString) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "message" attribute
   */
  @Override
  public void xsetMessage(final XmlString message)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlString target = null;
      target = (XmlString) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      if (target == null) {
        target = (XmlString) this.get_store().add_attribute_user(
          PROPERTY_QNAME[2]);
      }
      target.set(message);
    }
  }
}
