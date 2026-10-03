package org.apache.commons.collections.map;

 import java.util.Locale;
 import java.util.Map;
 import java.util.HashMap;

 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertNotNull;

 /**
  * Tests that expose the locale-sensitive bug in CaseInsensitiveMap.convertKey().
  * The buggy implementation uses String.toLowerCase() without a fixed Locale,
  * causing failures when the JVM default Locale is, for example, Turkish.
  */
 public class CaseInsensitiveMapLocaleBugTest {

     private Locale originalLocale;

     @Before
     public void setUp() {
         originalLocale = Locale.getDefault();
     }

     @After
     public void tearDown() {
         Locale.setDefault(originalLocale);
     }

     /**
      * Basic case-insensitivity under the English locale.
      */
     @Test
     public void testCaseInsensitivityUnderEnglishLocale() {
         Locale.setDefault(Locale.ENGLISH);
         CaseInsensitiveMap map = new CaseInsensitiveMap();
         map.put("key", "value");
         assertEquals("value", map.get("KEY"));
         assertEquals("value", map.get("Key"));
         assertEquals("value", map.get("kEy"));
     }

     /**
      * Put under English locale, then retrieve under Turkish locale.
      * This directly reproduces the COLLECTIONS-294 bug.
      */
     @Test
     public void testPutEnglishGetTurkishLocaleIndependence() {
         Locale.setDefault(Locale.ENGLISH);
         CaseInsensitiveMap map = new CaseInsensitiveMap();
         map.put("SomeKey", "expectedValue");
         // Switch to Turkish locale for retrieval
         Locale.setDefault(new Locale("tr", "TR"));
         assertEquals("expectedValue", map.get("SOMEKEY"));
         assertEquals("expectedValue", map.get("somekey"));
         assertEquals("expectedValue", map.get("SomeKey"));
     }

     /**
      * Put under Turkish locale, then retrieve under English locale.
      */
     @Test
     public void testPutTurkishGetEnglishLocaleIndependence() {
         Locale.setDefault(new Locale("tr", "TR"));
         CaseInsensitiveMap map = new CaseInsensitiveMap();
         map.put("TestKey", "expectedValue");
         // Switch to English locale for retrieval
         Locale.setDefault(Locale.ENGLISH);
         assertEquals("expectedValue", map.get("testkey"));
         assertEquals("expectedValue", map.get("TESTKEY"));
         assertEquals("expectedValue", map.get("TestKey"));
     }

     /**
      * Turkish dotless-i exposes locale-sensitive folding.
      * Under tr locale 'I'.toLowerCase() == 'ı' (dotless i),
      * but the map should behave consistently regardless of locale.
      *
      * Since the buggy convertKey() uses the current Locale at insertion time,
      * inserting under English then switching to Turkish for retrieval exposes the bug.
      * The key "I" inserted under English becomes "i". Under Turkish, "I".toLowerCase()
      * becomes "ı", so retrieval fails.
      */
     @Test
     public void testTurkishDotlessIFolding() {
         Locale.setDefault(Locale.ENGLISH);
         CaseInsensitiveMap map = new CaseInsensitiveMap();
         map.put("I", "capital-I");
         map.put("i", "lower-i");
         // Under English locale, retrieval is straightforward.
         assertEquals("capital-I", map.get("I"));
         assertEquals("lower-i", map.get("i"));
         // Switch to Turkish – retrieval for lower-i must still work;
         // "I" retrieval will fail because of the Turkish lowercasing bug.
         Locale.setDefault(new Locale("tr", "TR"));
         assertEquals("lower-i", map.get("i"));
         // The bug: under Turkish, "I".toLowerCase() = "ı", so get("I") returns null
         // (the stored key is "i"). This is the expected buggy behavior.
         assertNull("Bug: under Turkish locale, 'I' lowercases to 'ı' instead of 'i'",
map.get("I"));
     }

     /**
      * A value put under English using uppercase 'I' should be retrievable
      * with lowercase 'i' even after switching to Turkish locale.
      */
     @Test
     public void testUpperCaseIUnderEnglishThenTurkish() {
         Locale.setDefault(Locale.ENGLISH);
         CaseInsensitiveMap map = new CaseInsensitiveMap();
         map.put("ITEM", "value");
         assertEquals("value", map.get("item"));
         // Switch to Turkish
         Locale.setDefault(new Locale("tr", "TR"));
         assertEquals("value", map.get("item"));
         assertEquals("value", map.get("ITEM"));
         assertEquals("value", map.get("ItEm"));
     }

     /**
      * Null keys are supported and should remain retrievable.
      */
     @Test
     public void testNullKeySupport() {
         CaseInsensitiveMap map = new CaseInsensitiveMap();
         map.put(null, "nullValue");
         assertEquals("nullValue", map.get(null));
     }

     /**
      * Constructor that copies from another Map should apply case-insensitive
      * conversion immediately, potentially merging entries that differ only in case.
      */
     @Test
     public void testCopyConstructorMergesCaseInsensitively() {
         Map<String, String> source = new HashMap<String, String>();
         source.put("Key", "first");
         source.put("KEY", "second");
         // The second put should overwrite the first in the CaseInsensitiveMap.
         CaseInsensitiveMap map = new CaseInsensitiveMap(source);
         assertEquals(1, map.size());
         assertEquals("second", map.get("key"));
     }

     /**
      * keySet() returns the converted keys as stored internally.
      * Since convertKey() lowercases, the key "MiXeD" becomes "mixed".
      * The keySet will contain "mixed", not "MiXeD".
      */
     @Test
     public void testKeySetContainsLowercaseKeys() {
         CaseInsensitiveMap map = new CaseInsensitiveMap();
         map.put("MiXeD", "value");
         map.put(null, "nullVal");
         assertTrue(map.keySet().contains("mixed"));
         assertFalse(map.keySet().contains("MiXeD"));
         assertTrue(map.keySet().contains(null));
     }

     /**
      * containsKey must be locale-independent.
      */
     @Test
     public void testContainsKeyLocaleIndependence() {
         Locale.setDefault(Locale.ENGLISH);
         CaseInsensitiveMap map = new CaseInsensitiveMap();
         map.put("StorageKey", "payload");
         Locale.setDefault(new Locale("tr", "TR"));
         assertTrue("Must find key under Turkish locale", map.containsKey("storagekey"));
         assertTrue("Must find key under Turkish locale", map.containsKey("STORAGEKEY"));
         assertTrue("Must find key under Turkish locale", map.containsKey("StorageKey"));
     }

     /**
      * Cloned map must retain locale-independent case-insensitivity.
      */
     @Test
     public void testClonePreservesLocaleIndependentBehavior() {
         Locale.setDefault(Locale.ENGLISH);
         CaseInsensitiveMap original = new CaseInsensitiveMap();
         original.put("CloneKey", "cloneValue");
         CaseInsensitiveMap clone = (CaseInsensitiveMap) original.clone();
         // Verify clone works under English
         assertEquals("cloneValue", clone.get("clonekey"));
         // Switch to Turkish and the clone must still work
         Locale.setDefault(new Locale("tr", "TR"));
         assertEquals("cloneValue", clone.get("CLONEKEY"));
     }

     /**
      * Map should remain usable and consistent after multiple locale switches.
      */
     @Test
     public void testRepeatedLocaleSwitches() {
         CaseInsensitiveMap map = new CaseInsensitiveMap();
         // Put under English
         Locale.setDefault(Locale.ENGLISH);
         map.put("Keep", "first");
         // Put under Turkish
         Locale.setDefault(new Locale("tr", "TR"));
         map.put("Append", "second");
         // Retrieve under English
         Locale.setDefault(Locale.ENGLISH);
         assertEquals("first", map.get("KEEP"));
         assertEquals("second", map.get("APPEND"));
         // Retrieve under Turkish
         Locale.setDefault(new Locale("tr", "TR"));
         assertEquals("first", map.get("keep"));
         assertEquals("second", map.get("append"));
     }
 }
