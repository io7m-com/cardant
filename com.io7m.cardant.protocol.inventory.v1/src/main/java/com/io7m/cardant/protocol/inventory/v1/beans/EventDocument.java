/*
 * An XML document type.
 * Localname: Event
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.EventDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one Event(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface EventDocument extends MessageDocument
{
  DocumentFactory<EventDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "event50bfdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "Event" element
   */
  EventType getEvent();

  /**
   * Sets the "Event" element
   */
  void setEvent(EventType event);

  /**
   * Appends and returns a new empty "Event" element
   */
  EventType addNewEvent();
}
