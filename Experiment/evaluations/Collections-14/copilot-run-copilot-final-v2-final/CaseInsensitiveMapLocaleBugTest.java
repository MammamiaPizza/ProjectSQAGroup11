package org.apache.commons.collections.map;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CaseInsensitiveMapLocaleBugTest {
    private Locale savedLocale;

 @Before
 public void setUp() {
     savedLocale = Locale.getDefault();
 }

 @After
 public void tearDown() {
     Locale.setDefault(savedLocale);
 }

 private void setTurkishLocale() {
     Locale.setDefault(new Locale("tr", "TR"));
 }

 private void setEnglishLocale() {
     Locale.setDefault(Locale.ENGLISH);
 }

 @Test
 public void testTurkishLocaleDotlessI() {
     setTurkishLocale();
     CaseInsensitiveMap map = new CaseInsensitiveMap();
     map.put("i", "value");
     assertEquals("value", map.get("I"));
 }

 @Test
 public void testTurkishLocaleDotlessIReverse() {
     setTurkishLocale();
     CaseInsensitiveMap map = new CaseInsensitiveMap();
     map.put("I", "value");
     assertEquals("value", map.get("i"));
 }

 @Test
 public void testTurkishLocaleRemoveDotlessI() {
     setTurkishLocale();
     CaseInsensitiveMap map = new CaseInsensitiveMap();
     map.put("I", "value");
     assertEquals("value", map.remove("i"));
     assertNull(map.get("I"));
 }

 @Test
 public void testTurkishLocaleContainsKeyDotlessI() {
     setTurkishLocale();
     CaseInsensitiveMap map = new CaseInsensitiveMap();
     map.put("I", "value");
     assertTrue(map.containsKey("i"));
 }

 @Test
 public void testEnglishLocaleBaseline() {
     setEnglishLocale();
     CaseInsensitiveMap map = new CaseInsensitiveMap();
     map.put("Key", "value");
     assertEquals("value", map.get("KEY"));
 }

 @Test
 public void testOverwriteDifferentCase() {
     CaseInsensitiveMap map = new CaseInsensitiveMap();
     map.put("key", "first");
     map.put("KEY", "second");
     assertEquals("second", map.get("key"));
     assertEquals("second", map.get("KEY"));
 }

 @Test
 public void testNullKey() {
     CaseInsensitiveMap map = new CaseInsensitiveMap();
     map.put(null, "nullValue");
     assertEquals("nullValue", map.get(null));
     assertTrue(map.containsKey(null));
 }

 @Test
 public void testEmptyStringKey() {
     CaseInsensitiveMap map = new CaseInsensitiveMap();
     map.put("", "empty");
     assertEquals("empty", map.get(""));
 }

 @Test
 public void testKeySetReturnsLowercase() {
     CaseInsensitiveMap map = new CaseInsensitiveMap();
     map.put("Key", "value");
     assertTrue(map.keySet().contains("key"));
 }

 @Test
 public void testCopyConstructorCaseInsensitive() {
     Map<String, String> source = new HashMap<String, String>();
     source.put("Key", "value");
     source.put("KEY", "value2"); // overwrites because constructor lowercases keys
     CaseInsensitiveMap map = new CaseInsensitiveMap(source);
     assertEquals(1, map.size());
     assertEquals("value2", map.get("key"));
 }

 @Test
 public void testTurkishLocaleDottedI() {
     setTurkishLocale();
     CaseInsensitiveMap map = new CaseInsensitiveMap();
     map.put("İ", "dotted");
     assertEquals("dotted", map.get("i"));
 }

 @Test
 public void testTurkishLocaleIInWord() {
     setTurkishLocale();
     CaseInsensitiveMap map = new CaseInsensitiveMap();
     map.put("Title", "title");
     assertEquals("title", map.get("TITLE"));
 }

@Test
 public void testConstructorWithInitialCapacity() {
     CaseInsensitiveMap map = new CaseInsensitiveMap(10);
     assertTrue(map.isEmpty());
 }

 @Test
 public void testConstructorWithInitialCapacityAndLoadFactor() {
     CaseInsensitiveMap map = new CaseInsensitiveMap(10, 0.75f);
     assertTrue(map.isEmpty());
 }

 @Test
 public void testSerializationRoundtrip() throws Exception {
     CaseInsensitiveMap map = new CaseInsensitiveMap();
     map.put("key", "value");
     map.put("Key", "value2");

     java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
     java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
     oos.writeObject(map);
     oos.close();

     java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
     java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
     CaseInsensitiveMap deserialized = (CaseInsensitiveMap) ois.readObject();
     ois.close();

     assertEquals(1, deserialized.size());
     assertEquals("value2", deserialized.get("KEY"));
     assertEquals("value2", deserialized.get("key"));
 }

 @Test
 public void testSerializationPreservesLocaleIndependence() throws Exception {
     java.util.Locale turkish = new java.util.Locale("tr");
     java.util.Locale defaultLocale = java.util.Locale.getDefault();
     try {
         java.util.Locale.setDefault(turkish);

         CaseInsensitiveMap map = new CaseInsensitiveMap();
         map.put("i", "lowercaseI");
         map.put("I", "uppercaseI");

         java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
         java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
         oos.writeObject(map);
         oos.close();

         java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
         java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
         CaseInsensitiveMap deserialized = (CaseInsensitiveMap) ois.readObject();
         ois.close();

         assertEquals(1, deserialized.size());
         assertEquals("uppercaseI", deserialized.get("i"));
         assertEquals("uppercaseI", deserialized.get("I"));
     } finally {
         java.util.Locale.setDefault(defaultLocale);
     }
 }
}
