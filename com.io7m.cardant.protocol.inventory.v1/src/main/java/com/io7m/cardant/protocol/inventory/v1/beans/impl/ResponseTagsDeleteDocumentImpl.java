/*
 * An XML document type.
 * Localname: ResponseTagsDelete
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseTagsDelete(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseTagsDeleteDocumentImpl extends ResponseDocumentImpl implements
  ResponseTagsDeleteDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsDelete"),
  };

  public ResponseTagsDeleteDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseTagsDelete" element
   */
  @Override
  public ResponseTagsDeleteType getResponseTagsDelete()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseTagsDeleteType target = null;
      target = (ResponseTagsDeleteType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseTagsDelete" element
   */
  @Override
  public void setResponseTagsDelete(final ResponseTagsDeleteType responseTagsDelete)
  {
    this.generatedSetterHelperImpl(
      responseTagsDelete,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseTagsDelete" element
   */
  @Override
  public ResponseTagsDeleteType addNewResponseTagsDelete()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseTagsDeleteType target = null;
      target = (ResponseTagsDeleteType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
