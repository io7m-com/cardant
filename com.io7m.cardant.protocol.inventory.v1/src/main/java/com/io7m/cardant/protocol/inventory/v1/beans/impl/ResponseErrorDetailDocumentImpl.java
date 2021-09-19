/*
 * An XML document type.
 * Localname: ResponseErrorDetail
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseErrorDetail(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseErrorDetailDocumentImpl extends XmlComplexContentImpl implements
  ResponseErrorDetailDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ResponseErrorDetail"),
  };

  public ResponseErrorDetailDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseErrorDetail" element
   */
  @Override
  public ResponseErrorDetailType getResponseErrorDetail()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseErrorDetailType target = null;
      target = (ResponseErrorDetailType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseErrorDetail" element
   */
  @Override
  public void setResponseErrorDetail(final ResponseErrorDetailType responseErrorDetail)
  {
    this.generatedSetterHelperImpl(
      responseErrorDetail,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseErrorDetail" element
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
}
