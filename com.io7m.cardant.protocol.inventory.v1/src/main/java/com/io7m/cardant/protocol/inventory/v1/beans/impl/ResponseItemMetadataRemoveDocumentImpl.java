/*
 * An XML document type.
 * Localname: ResponseItemMetadataRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseItemMetadataRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemMetadataRemoveDocumentImpl extends ResponseDocumentImpl implements
  ResponseItemMetadataRemoveDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemMetadataRemove"),
  };

  public ResponseItemMetadataRemoveDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseItemMetadataRemove" element
   */
  @Override
  public ResponseItemMetadataRemoveType getResponseItemMetadataRemove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseItemMetadataRemoveType target = null;
      target = (ResponseItemMetadataRemoveType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseItemMetadataRemove" element
   */
  @Override
  public void setResponseItemMetadataRemove(final ResponseItemMetadataRemoveType responseItemMetadataRemove)
  {
    this.generatedSetterHelperImpl(
      responseItemMetadataRemove,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseItemMetadataRemove" element
   */
  @Override
  public ResponseItemMetadataRemoveType addNewResponseItemMetadataRemove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseItemMetadataRemoveType target = null;
      target = (ResponseItemMetadataRemoveType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
