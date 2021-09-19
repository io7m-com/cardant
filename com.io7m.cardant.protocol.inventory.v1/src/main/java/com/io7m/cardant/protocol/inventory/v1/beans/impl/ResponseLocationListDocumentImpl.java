/*
 * An XML document type.
 * Localname: ResponseLocationList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseLocationList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseLocationListDocumentImpl extends ResponseDocumentImpl implements
  ResponseLocationListDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ResponseLocationList"),
  };

  public ResponseLocationListDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseLocationList" element
   */
  @Override
  public ResponseLocationListType getResponseLocationList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseLocationListType target = null;
      target = (ResponseLocationListType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseLocationList" element
   */
  @Override
  public void setResponseLocationList(final ResponseLocationListType responseLocationList)
  {
    this.generatedSetterHelperImpl(
      responseLocationList,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseLocationList" element
   */
  @Override
  public ResponseLocationListType addNewResponseLocationList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseLocationListType target = null;
      target = (ResponseLocationListType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
