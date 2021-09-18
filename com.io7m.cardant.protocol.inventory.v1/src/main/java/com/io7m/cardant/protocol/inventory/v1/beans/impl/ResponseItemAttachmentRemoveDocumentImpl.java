/*
 * An XML document type.
 * Localname: ResponseItemAttachmentRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentRemoveType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseItemAttachmentRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemAttachmentRemoveDocumentImpl extends ResponseDocumentImpl implements
  ResponseItemAttachmentRemoveDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName(
      "urn:com.io7m.cardant.inventory:1",
      "ResponseItemAttachmentRemove"),
  };

  public ResponseItemAttachmentRemoveDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseItemAttachmentRemove" element
   */
  @Override
  public ResponseItemAttachmentRemoveType getResponseItemAttachmentRemove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseItemAttachmentRemoveType target = null;
      target = (ResponseItemAttachmentRemoveType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseItemAttachmentRemove" element
   */
  @Override
  public void setResponseItemAttachmentRemove(final ResponseItemAttachmentRemoveType responseItemAttachmentRemove)
  {
    this.generatedSetterHelperImpl(
      responseItemAttachmentRemove,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseItemAttachmentRemove" element
   */
  @Override
  public ResponseItemAttachmentRemoveType addNewResponseItemAttachmentRemove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseItemAttachmentRemoveType target = null;
      target = (ResponseItemAttachmentRemoveType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
