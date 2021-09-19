/*
 * An XML document type.
 * Localname: ResponseItemRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseItemRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemRemoveDocumentImpl extends ResponseDocumentImpl implements
  ResponseItemRemoveDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemRemove"),
  };

  public ResponseItemRemoveDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseItemRemove" element
   */
  @Override
  public ResponseItemRemoveType getResponseItemRemove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseItemRemoveType target = null;
      target = (ResponseItemRemoveType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseItemRemove" element
   */
  @Override
  public void setResponseItemRemove(final ResponseItemRemoveType responseItemRemove)
  {
    this.generatedSetterHelperImpl(
      responseItemRemove,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseItemRemove" element
   */
  @Override
  public ResponseItemRemoveType addNewResponseItemRemove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseItemRemoveType target = null;
      target = (ResponseItemRemoveType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
