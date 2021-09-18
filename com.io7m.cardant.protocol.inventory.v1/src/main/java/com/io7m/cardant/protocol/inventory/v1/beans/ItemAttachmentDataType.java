/*
 * XML Type:  ItemAttachmentDataType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ItemAttachmentDataType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is an atomic type that is a restriction of com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType.
 */
public interface ItemAttachmentDataType extends XmlBase64Binary
{
  SimpleTypeFactory<ItemAttachmentDataType> Factory = new SimpleTypeFactory<>(
    TypeSystemHolder.typeSystem,
    "itemattachmentdatatype101dtype");
  SchemaType type = Factory.getType();

}
