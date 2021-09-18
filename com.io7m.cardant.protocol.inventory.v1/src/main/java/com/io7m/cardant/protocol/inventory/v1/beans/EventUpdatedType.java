/*
 * XML Type:  EventUpdatedType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML EventUpdatedType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface EventUpdatedType extends EventType
{
  DocumentFactory<EventUpdatedType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "eventupdatedtype6e9ctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "Updated" element
   */
  UpdatedType getUpdated();

  /**
   * Sets the "Updated" element
   */
  void setUpdated(UpdatedType updated);

  /**
   * Appends and returns a new empty "Updated" element
   */
  UpdatedType addNewUpdated();

  /**
   * Gets the "Removed" element
   */
  RemovedType getRemoved();

  /**
   * Sets the "Removed" element
   */
  void setRemoved(RemovedType removed);

  /**
   * Appends and returns a new empty "Removed" element
   */
  RemovedType addNewRemoved();
}
