/*
 * An XML document type.
 * Localname: ResponseItemList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseItemList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemListDocumentImpl extends ResponseDocumentImpl implements
  ResponseItemListDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemList"),
  };

  public ResponseItemListDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseItemList" element
   */
  @Override
  public ResponseItemListType getResponseItemList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseItemListType target = null;
      target = (ResponseItemListType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseItemList" element
   */
  @Override
  public void setResponseItemList(final ResponseItemListType responseItemList)
  {
    this.generatedSetterHelperImpl(
      responseItemList,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseItemList" element
   */
  @Override
  public ResponseItemListType addNewResponseItemList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseItemListType target = null;
      target = (ResponseItemListType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
