/*
 * XML Type:  CommandItemGetType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML CommandItemGetType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandItemGetType extends CommandType
{
  DocumentFactory<CommandItemGetType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemgettype0285type");
  SchemaType type = Factory.getType();


  /**
   * Gets the "id" attribute
   */
  String getId();

  /**
   * Sets the "id" attribute
   */
  void setId(String id);

  /**
   * Gets (as xml) the "id" attribute
   */
  UUIDType xgetId();

  /**
   * Sets (as xml) the "id" attribute
   */
  void xsetId(UUIDType id);
}
