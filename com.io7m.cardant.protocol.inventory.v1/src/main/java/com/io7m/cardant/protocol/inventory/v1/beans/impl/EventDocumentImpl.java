/*
 * An XML document type.
 * Localname: Event
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.EventDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.EventDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.EventType;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;

import javax.xml.namespace.QName;

/**
 * A document containing one Event(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class EventDocumentImpl extends MessageDocumentImpl implements
  EventDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "Event"),
  };
  private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray(new QName[]{
      new QName("urn:com.io7m.cardant.inventory:1", "Event"),
      new QName("urn:com.io7m.cardant.inventory:1", "EventUpdated"),
    }),
  };

  public EventDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "Event" element
   */
  @Override
  public EventType getEvent()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      EventType target = null;
      target = (EventType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "Event" element
   */
  @Override
  public void setEvent(final EventType event)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      EventType target = null;
      target = (EventType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      if (target == null) {
        target = (EventType) this.get_store().add_element_user(
          PROPERTY_QNAME[0]);
      }
      target.set(event);
    }
  }

  /**
   * Appends and returns a new empty "Event" element
   */
  @Override
  public EventType addNewEvent()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      EventType target = null;
      target = (EventType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
