/*
 * An XML document type.
 * Localname: ItemMetadata
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ItemMetadata(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemMetadataDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataDocument {
    private static final long serialVersionUID = 1L;

    public ItemMetadataDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadata"),
    };


    /**
     * Gets the "ItemMetadata" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType getItemMetadata() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemMetadata" element
     */
    @Override
    public void setItemMetadata(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType itemMetadata) {
        generatedSetterHelperImpl(itemMetadata, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ItemMetadata" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType addNewItemMetadata() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
