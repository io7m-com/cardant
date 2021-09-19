/*
 * An XML document type.
 * Localname: ResponseTagList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseTagList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseTagListDocumentImpl extends ResponseDocumentImpl implements
  ResponseTagListDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagList"),
  };

  public ResponseTagListDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseTagList" element
   */
  @Override
  public ResponseTagListType getResponseTagList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseTagListType target = null;
      target = (ResponseTagListType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseTagList" element
   */
  @Override
  public void setResponseTagList(final ResponseTagListType responseTagList)
  {
    this.generatedSetterHelperImpl(
      responseTagList,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseTagList" element
   */
  @Override
  public ResponseTagListType addNewResponseTagList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseTagListType target = null;
      target = (ResponseTagListType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
