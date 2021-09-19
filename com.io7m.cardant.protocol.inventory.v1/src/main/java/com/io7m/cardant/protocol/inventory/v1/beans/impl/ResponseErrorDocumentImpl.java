/*
 * An XML document type.
 * Localname: ResponseError
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseError(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseErrorDocumentImpl extends ResponseDocumentImpl implements
  ResponseErrorDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ResponseError"),
  };

  public ResponseErrorDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseError" element
   */
  @Override
  public ResponseErrorType getResponseError()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseErrorType target = null;
      target = (ResponseErrorType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseError" element
   */
  @Override
  public void setResponseError(final ResponseErrorType responseError)
  {
    this.generatedSetterHelperImpl(
      responseError,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseError" element
   */
  @Override
  public ResponseErrorType addNewResponseError()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseErrorType target = null;
      target = (ResponseErrorType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
