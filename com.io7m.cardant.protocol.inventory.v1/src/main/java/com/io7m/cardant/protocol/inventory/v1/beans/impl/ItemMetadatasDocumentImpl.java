/*
 * An XML document type.
 * Localname: ItemMetadatas
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ItemMetadatas(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemMetadatasDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasDocument {
    private static final long serialVersionUID = 1L;

    public ItemMetadatasDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadatas"),
    };


    /**
     * Gets the "ItemMetadatas" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType getItemMetadatas() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemMetadatas" element
     */
    @Override
    public void setItemMetadatas(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType itemMetadatas) {
        generatedSetterHelperImpl(itemMetadatas, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ItemMetadatas" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType addNewItemMetadatas() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
