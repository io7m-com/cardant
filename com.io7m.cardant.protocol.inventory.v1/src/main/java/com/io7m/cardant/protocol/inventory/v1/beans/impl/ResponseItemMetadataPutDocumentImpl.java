/*
 * An XML document type.
 * Localname: ResponseItemMetadataPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseItemMetadataPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemMetadataPutDocumentImpl extends ResponseDocumentImpl implements
  ResponseItemMetadataPutDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemMetadataPut"),
  };

  public ResponseItemMetadataPutDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseItemMetadataPut" element
   */
  @Override
  public ResponseItemMetadataPutType getResponseItemMetadataPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseItemMetadataPutType target = null;
      target = (ResponseItemMetadataPutType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseItemMetadataPut" element
   */
  @Override
  public void setResponseItemMetadataPut(final ResponseItemMetadataPutType responseItemMetadataPut)
  {
    this.generatedSetterHelperImpl(
      responseItemMetadataPut,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseItemMetadataPut" element
   */
  @Override
  public ResponseItemMetadataPutType addNewResponseItemMetadataPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseItemMetadataPutType target = null;
      target = (ResponseItemMetadataPutType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
