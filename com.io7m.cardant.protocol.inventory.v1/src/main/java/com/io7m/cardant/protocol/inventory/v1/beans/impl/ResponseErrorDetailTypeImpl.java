/*
 * XML Type:  ResponseErrorDetailType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

import javax.xml.namespace.QName;

/**
 * An XML ResponseErrorDetailType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ResponseErrorDetailTypeImpl extends XmlComplexContentImpl implements
  ResponseErrorDetailType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("", "message"),
  };

  public ResponseErrorDetailTypeImpl(final SchemaType sType)
  {
    super(sType);
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
        PROPERTY_QNAME[0]);
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
        PROPERTY_QNAME[0]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[0]);
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
        PROPERTY_QNAME[0]);
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
        PROPERTY_QNAME[0]);
      if (target == null) {
        target = (XmlString) this.get_store().add_attribute_user(
          PROPERTY_QNAME[0]);
      }
      target.set(message);
    }
  }
}
