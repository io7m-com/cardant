/*
 * An XML document type.
 * Localname: ResponseTagsPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseTagsPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseTagsPutDocumentImpl extends ResponseDocumentImpl implements
  ResponseTagsPutDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsPut"),
  };

  public ResponseTagsPutDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseTagsPut" element
   */
  @Override
  public ResponseTagsPutType getResponseTagsPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseTagsPutType target = null;
      target = (ResponseTagsPutType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseTagsPut" element
   */
  @Override
  public void setResponseTagsPut(final ResponseTagsPutType responseTagsPut)
  {
    this.generatedSetterHelperImpl(
      responseTagsPut,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseTagsPut" element
   */
  @Override
  public ResponseTagsPutType addNewResponseTagsPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseTagsPutType target = null;
      target = (ResponseTagsPutType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
