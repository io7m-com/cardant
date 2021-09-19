/*
 * An XML document type.
 * Localname: Message
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.MessageDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one Message(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface MessageDocument extends XmlObject
{
  DocumentFactory<MessageDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "messaged2b2doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "Message" element
   */
  MessageType getMessage();

  /**
   * Sets the "Message" element
   */
  void setMessage(MessageType message);

  /**
   * Appends and returns a new empty "Message" element
   */
  MessageType addNewMessage();
}
