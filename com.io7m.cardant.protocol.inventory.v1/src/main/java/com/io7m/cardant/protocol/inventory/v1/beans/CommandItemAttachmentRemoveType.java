/*
 * XML Type:  CommandItemAttachmentRemoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML CommandItemAttachmentRemoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandItemAttachmentRemoveType extends CommandType
{
  DocumentFactory<CommandItemAttachmentRemoveType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemattachmentremovetypeef2atype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "item" attribute
   */
  String getItem();

  /**
   * Sets the "item" attribute
   */
  void setItem(String item);

  /**
   * Gets (as xml) the "item" attribute
   */
  UUIDType xgetItem();

  /**
   * Sets (as xml) the "item" attribute
   */
  void xsetItem(UUIDType item);

  /**
   * Gets the "attachment" attribute
   */
  String getAttachment();

  /**
   * Sets the "attachment" attribute
   */
  void setAttachment(String attachment);

  /**
   * Gets (as xml) the "attachment" attribute
   */
  UUIDType xgetAttachment();

  /**
   * Sets (as xml) the "attachment" attribute
   */
  void xsetAttachment(UUIDType attachment);
}
