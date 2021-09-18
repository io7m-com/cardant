/*
 * An XML document type.
 * Localname: EventUpdated
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one EventUpdated(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class EventUpdatedDocumentImpl extends EventDocumentImpl implements
  EventUpdatedDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "EventUpdated"),
  };

  public EventUpdatedDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "EventUpdated" element
   */
  @Override
  public EventUpdatedType getEventUpdated()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      EventUpdatedType target = null;
      target = (EventUpdatedType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "EventUpdated" element
   */
  @Override
  public void setEventUpdated(final EventUpdatedType eventUpdated)
  {
    this.generatedSetterHelperImpl(
      eventUpdated,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "EventUpdated" element
   */
  @Override
  public EventUpdatedType addNewEventUpdated()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      EventUpdatedType target = null;
      target = (EventUpdatedType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
