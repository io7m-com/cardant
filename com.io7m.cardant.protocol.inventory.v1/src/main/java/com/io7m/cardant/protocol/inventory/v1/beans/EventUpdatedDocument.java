/*
 * An XML document type.
 * Localname: EventUpdated
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one EventUpdated(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface EventUpdatedDocument extends EventDocument
{
  DocumentFactory<EventUpdatedDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "eventupdated65a6doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "EventUpdated" element
   */
  EventUpdatedType getEventUpdated();

  /**
   * Sets the "EventUpdated" element
   */
  void setEventUpdated(EventUpdatedType eventUpdated);

  /**
   * Appends and returns a new empty "EventUpdated" element
   */
  EventUpdatedType addNewEventUpdated();
}
