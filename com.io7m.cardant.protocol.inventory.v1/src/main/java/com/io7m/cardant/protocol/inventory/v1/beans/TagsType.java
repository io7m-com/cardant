/*
 * XML Type:  TagsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TagsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

import java.util.List;


/**
 * An XML TagsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface TagsType extends XmlObject
{
  DocumentFactory<TagsType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "tagstypebd44type");
  SchemaType type = Factory.getType();


  /**
   * Gets a List of "Tag" elements
   */
  List<TagType> getTagList();

  /**
   * Gets array of all "Tag" elements
   */
  TagType[] getTagArray();

  /**
   * Sets array of all "Tag" element
   */
  void setTagArray(TagType[] tagArray);

  /**
   * Gets ith "Tag" element
   */
  TagType getTagArray(int i);

  /**
   * Returns number of "Tag" element
   */
  int sizeOfTagArray();

  /**
   * Sets ith "Tag" element
   */
  void setTagArray(
    int i,
    TagType tag);

  /**
   * Inserts and returns a new empty value (as xml) as the ith "Tag" element
   */
  TagType insertNewTag(int i);

  /**
   * Appends and returns a new empty value (as xml) as the last "Tag" element
   */
  TagType addNewTag();

  /**
   * Removes the ith "Tag" element
   */
  void removeTag(int i);
}
