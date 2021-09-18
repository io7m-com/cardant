/*
 * An XML document type.
 * Localname: Response
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseType;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;

import javax.xml.namespace.QName;

/**
 * A document containing one Response(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseDocumentImpl extends MessageDocumentImpl implements
  ResponseDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "Response"),
  };
  private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray(new QName[]{
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagList"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemRemove"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemList"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsDelete"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "ResponseItemMetadataRemove"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemMetadataPut"),
      new QName("urn:com.io7m.cardant.inventory:1", "Response"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "ResponseLoginUsernamePassword"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "ResponseItemAttachmentPut"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemCreate"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "ResponseItemAttachmentRemove"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsPut"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemUpdate"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemGet"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseError"),
    }),
  };

  public ResponseDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "Response" element
   */
  @Override
  public ResponseType getResponse()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseType target = null;
      target = (ResponseType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "Response" element
   */
  @Override
  public void setResponse(final ResponseType response)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseType target = null;
      target = (ResponseType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      if (target == null) {
        target = (ResponseType) this.get_store().add_element_user(
          PROPERTY_QNAME[0]);
      }
      target.set(response);
    }
  }

  /**
   * Appends and returns a new empty "Response" element
   */
  @Override
  public ResponseType addNewResponse()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ResponseType target = null;
      target = (ResponseType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
