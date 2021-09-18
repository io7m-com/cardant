/*
 * An XML document type.
 * Localname: Updated
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.UpdatedDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.UpdatedDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.UpdatedType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one Updated(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class UpdatedDocumentImpl extends XmlComplexContentImpl implements
  UpdatedDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "Updated"),
  };

  public UpdatedDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "Updated" element
   */
  @Override
  public UpdatedType getUpdated()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UpdatedType target = null;
      target = (UpdatedType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "Updated" element
   */
  @Override
  public void setUpdated(final UpdatedType updated)
  {
    this.generatedSetterHelperImpl(
      updated,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "Updated" element
   */
  @Override
  public UpdatedType addNewUpdated()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UpdatedType target = null;
      target = (UpdatedType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
