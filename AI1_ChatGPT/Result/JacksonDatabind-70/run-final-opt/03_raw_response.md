package com.fasterxml.jackson.databind.deser.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BeanPropertyMapCaseInsensitiveRemovalTest
{
    public static class AddressBean {
        public String businessAddress;
        public String zipCode;
    }

    private List<SettableBeanProperty> properties() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = mapper.getFactory().createParser("{}");
        try {
            DefaultDeserializationContext ctxt = ((DefaultDeserializationContext) mapper.getDeserializationContext())
                    .createInstance(mapper.getDeserializationConfig(), parser, null);
            JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(
                    mapper.constructType(AddressBean.class));

            assertTrue(deser instanceof BeanDeserializerBase);

            List<SettableBeanProperty> result = new ArrayList<SettableBeanProperty>();
            Iterator<SettableBeanProperty> it = ((BeanDeserializerBase) deser).properties();
            while (it.hasNext()) {
                result.add(it.next());
            }
            assertEquals(2, result.size());
            return result;
        } finally {
            parser.close();
        }
    }

    private BeanPropertyMap map(boolean caseInsensitive) throws Exception {
        return BeanPropertyMap.construct(properties(), caseInsensitive);
    }

    @Test
    public void removeUsesCanonicalNameForCaseInsensitiveMap() throws Exception {
        BeanPropertyMap map = map(true);
        SettableBeanProperty business = map.find("BUSINESSADDRESS");
        SettableBeanProperty zip = map.find("zipcode");

        assertSame(business, map.find("businessAddress"));
        assertSame(zip, map.find("ZIPCODE"));

        SettableBeanProperty[] before = map.getPropertiesInInsertionOrder();
        int businessPosition = -1;
        for (int i = 0; i < before.length; ++i) {
            if (before[i] == business) {
                businessPosition = i;
                break;
            }
        }
        assertTrue(businessPosition >= 0);

        map.remove(business);

        assertEquals(1, map.size());
        assertNull(map.find("businessAddress"));
        assertNull(map.find("BUSINESSADDRESS"));
        assertNull(map.find("BusinessAddress"));
        assertSame(zip, map.find("zipCode"));
        assertNull(map.getPropertiesInInsertionOrder()[businessPosition]);
    }

    @Test
    public void removalFromCaseInsensitiveMapRetainsRemainingIteratorProperty() throws Exception {
        BeanPropertyMap map = map(true);
        SettableBeanProperty business = map.find("businessAddress");
        SettableBeanProperty zip = map.find("zipCode");

        map.remove(business);

        Iterator<SettableBeanProperty> it = map.iterator();
        assertTrue(it.hasNext());
        assertSame(zip, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void withCaseInsensitivitySupportsCaseVariantLookupAndRemoval() throws Exception {
        BeanPropertyMap sensitive = map(false);
        BeanPropertyMap insensitive = sensitive.withCaseInsensitivity(true);

        SettableBeanProperty business = insensitive.find("BUSINESSADDRESS");
        assertSame(sensitive.find("businessAddress"), business);

        insensitive.remove(business);

        assertEquals(1, insensitive.size());
        assertNull(insensitive.find("businessAddress"));
        assertNull(insensitive.find("BUSINESSADDRESS"));
        assertTrue(insensitive.find("zipCode") != null);
    }

    @Test
    public void caseSensitiveMapRequiresExactLookupNameAndRemovesExactProperty() throws Exception {
        BeanPropertyMap map = map(false);
        SettableBeanProperty business = map.find("businessAddress");
        SettableBeanProperty zip = map.find("zipCode");

        assertNull(map.find("BUSINESSADDRESS"));
        assertSame(business, map.find("businessAddress"));

        map.remove(business);

        assertEquals(1, map.size());
        assertNull(map.find("businessAddress"));
        assertNull(map.find("BUSINESSADDRESS"));
        assertSame(zip, map.find("zipCode"));
    }

    @Test
    public void findRejectsNullPropertyName() throws Exception {
        BeanPropertyMap map = map(true);

        try {
            map.find((String) null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null property name"));
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException for null property name");
    }
}