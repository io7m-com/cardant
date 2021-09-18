/*
 * An XML document type.
 * Localname: ResponseItemCreate
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseItemCreate(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemCreateDocumentImpl extends ResponseDocumentImpl implements
  ResponseItemCreateDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemCreate"),
  };

  public ResponseItemCreateDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseItemCreate" element
   */
  @Override
  public ResponseItemCreateType getResponseItemCreate()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseItemCreateType target = null;
      target = (ResponseItemCreateType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseItemCreate" element
   */
  @Override
  public void setResponseItemCreate(final ResponseItemCreateType responseItemCreate)
  {
    this.generatedSetterHelperImpl(
      responseItemCreate,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseItemCreate" element
   */
  @Override
  public ResponseItemCreateType addNewResponseItemCreate()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseItemCreateType target = null;
      target = (ResponseItemCreateType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
