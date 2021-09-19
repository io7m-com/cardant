/*
 * An XML document type.
 * Localname: ResponseLocationPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseLocationPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseLocationPutDocumentImpl extends ResponseDocumentImpl implements
  ResponseLocationPutDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ResponseLocationPut"),
  };

  public ResponseLocationPutDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseLocationPut" element
   */
  @Override
  public ResponseLocationPutType getResponseLocationPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseLocationPutType target = null;
      target = (ResponseLocationPutType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseLocationPut" element
   */
  @Override
  public void setResponseLocationPut(final ResponseLocationPutType responseLocationPut)
  {
    this.generatedSetterHelperImpl(
      responseLocationPut,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseLocationPut" element
   */
  @Override
  public ResponseLocationPutType addNewResponseLocationPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseLocationPutType target = null;
      target = (ResponseLocationPutType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
