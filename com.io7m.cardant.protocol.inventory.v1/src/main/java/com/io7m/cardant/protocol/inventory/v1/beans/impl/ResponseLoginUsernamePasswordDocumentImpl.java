/*
 * An XML document type.
 * Localname: ResponseLoginUsernamePassword
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ResponseLoginUsernamePassword(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseLoginUsernamePasswordDocumentImpl extends ResponseDocumentImpl implements
  ResponseLoginUsernamePasswordDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName(
      "urn:com.io7m.cardant.inventory:1",
      "ResponseLoginUsernamePassword"),
  };

  public ResponseLoginUsernamePasswordDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ResponseLoginUsernamePassword" element
   */
  @Override
  public ResponseLoginUsernamePasswordType getResponseLoginUsernamePassword()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseLoginUsernamePasswordType target = null;
      target = (ResponseLoginUsernamePasswordType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ResponseLoginUsernamePassword" element
   */
  @Override
  public void setResponseLoginUsernamePassword(final ResponseLoginUsernamePasswordType responseLoginUsernamePassword)
  {
    this.generatedSetterHelperImpl(
      responseLoginUsernamePassword,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ResponseLoginUsernamePassword" element
   */
  @Override
  public ResponseLoginUsernamePasswordType addNewResponseLoginUsernamePassword()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseLoginUsernamePasswordType target = null;
      target = (ResponseLoginUsernamePasswordType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
