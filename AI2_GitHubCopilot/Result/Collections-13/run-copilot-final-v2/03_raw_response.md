package org.apache.commons.collections;

 import static org.junit.Assert.*;
 import org.junit.Before;
 import org.junit.Test;

 import java.util.HashMap;
 import java.util.Map;
 import java.util.Properties;

 /**
  * Tests for bug COLLECTIONS-299 in ExtendedProperties.
  * Focuses on null value handling in convertProperties, put, and putAll.
  */
 public class TestExtendedPropertiesBug13 {

     private ExtendedProperties ep;

     @Before
     public void setUp() {
         ep = new ExtendedProperties();
     }

     @Test
     public void testConvertPropertiesEmpty() {
         ExtendedProperties result = ExtendedProperties.convertProperties(new Properties());
         assertNotNull("result must not be null", result);
         assertTrue("empty input should produce empty object", result.isEmpty());
     }

     @Test
     public void testConvertPropertiesNormal() {
         Properties props = new Properties();
         props.setProperty("key", "value");
         ExtendedProperties result = ExtendedProperties.convertProperties(props);
         assertEquals("value", result.getProperty("key"));
     }

     @Test
     public void testConvertPropertiesNullValue() {
         Properties props = new AllowNullValueProperties();
         props.put("nullKey", null);

         // should not throw NullPointerException
         ExtendedProperties result = ExtendedProperties.convertProperties(props);
         assertNotNull(result);
         // The contract does not mandate preserving null entries; either absent or null is
acceptable.
         assertNull(result.getProperty("nullKey"));
     }

     @Test
     public void testConvertPropertiesMixedNull() {
         Properties props = new AllowNullValueProperties();
         props.put("goodKey", "goodValue");
         props.put("nullKey", null);

         ExtendedProperties result = ExtendedProperties.convertProperties(props);
         assertEquals("goodValue", result.getProperty("goodKey"));
         assertNull("null key must be absent or null", result.getProperty("nullKey"));
     }

     @Test(expected = NullPointerException.class)
     public void testConvertPropertiesNullInput() {
         ExtendedProperties.convertProperties(null);
     }

     @Test(expected = NullPointerException.class)
     public void testPutNullKey() {
         ep.put(null, "value");
     }

     @Test(expected = NullPointerException.class)
     public void testPutNullValue() {
         ep.put("key", null);
     }

     @Test(expected = NullPointerException.class) // or it may throw NPE from Hashtable.put
     public void testPutAllNullValue() {
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("key", null);
         ep.putAll(map);
     }

     /**
      * A Properties subclass that allows null values, for testing the robustness
      * of convertProperties against unexpected null entries.
      */
     static class AllowNullValueProperties extends Properties {
         private final HashMap<Object, Object> delegate = new HashMap<>();

         @Override
         public synchronized Object put(Object key, Object value) {
             return delegate.put(key, value);
         }

         @Override
         public synchronized Object get(Object key) {
             return delegate.get(key);
         }

         @Override
         public synchronized boolean isEmpty() {
             return delegate.isEmpty();
         }

         @Override
         public synchronized int size() {
             return delegate.size();
         }

         @Override
         public java.util.Set<Object> keySet() {
             return delegate.keySet();
         }
     }
 }