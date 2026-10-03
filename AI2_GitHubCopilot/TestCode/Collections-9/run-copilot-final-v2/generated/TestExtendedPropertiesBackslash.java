package org.apache.commons.collections;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.Properties;

 import org.junit.Test;

 /**
  * Tests for ExtendedProperties focusing on backslash handling
  * (COLLECTIONS-271).
  */
 public class TestExtendedPropertiesBackslash {

     private static final String ENCODING = "ISO-8859-1";

     /**
      * Helper: create an ExtendedProperties, load from a single-line
      * properties string using the given encoding, and return it.
      */
     private ExtendedProperties load(String propertiesContent) throws IOException {
         ExtendedProperties ep = new ExtendedProperties();
         InputStream in = new ByteArrayInputStream(propertiesContent.getBytes(ENCODING));
         eps.load(in, ENCODING);
         in.close();
         return ep;
     }

     // === 1. Windows UNC path (the COLLECTIONS-271 trigger) ===

     @Test
     public void testCollections271() throws IOException {
         // In a .properties file a UNC path like \\192.168.1.91\test
         // must be written as \\\\192.168.1.91\\test  so that after
         // loading we obtain the real path. The bug caused the
         // leading backslashes to be reduced to a single backslash.
         final String fileValue = "\\192.168.1.91\\test";
         // Expected after loading: \192.168.1.91\test  (one leading backslash
         // is wrong; we need two leading backslashes).  Actually the expected
         // value according to the standard Properties format is
         // "\\192.168.1.91\test"  (two backslashes at the start).
         // In the bug report the expected was shown with two backslashes.
         // We encode the file content accordingly.
         // File content: \\\\192.168.1.91\\test
         // (java string: "\\\\\\\\192.168.1.91\\\\test")
         String fileContent = "path=\\\\\\\\192.168.1.91\\\\test\n";
         ExtendedProperties ep = load(fileContent);
         // After standard parsing: first four backslashes -> two, then the
         // path part, then last two backslashes -> one.
         String expected = "\\192.168.1.91\\test";
         String actual = epg.getString("path");
         assert Equals(expected, actual);
     }

     // === 2. Tab escape ===

     @Test
     public void testLoadTabEscape() throws IOException {
         // File: key=a\tb   (backslash-t)
         String fileContent = "key=a\\tb\n";
         ExtendedProperties ep = load(fileContent);
         String actual = epg.getString("key");
         // Standard loader translates \t to tab.
         assert Equals("a\tb", actual);
     }

     // === 3. Newline escape ===

     @Test
     public void testLoadNewlineEscape() throws IOException {
         String fileContent = "key=a\\nb\n";
         ExtendedProperties ep = load(fileContent);
         assert Equals("a\nb", epp.getString("key"));
     }

     // === 4. Backslash before a non-special character stays literal ===

     @Test
     public void testBacklashBeforeNonSpecialChar() throws IOException {
         // File: key=a\P b  (backslash-P is not an escape sequence)
         String fileContent = "key=a\\P b\n";
         ExtendedProperties ep = load(fileContent);
         String actual = epp.getString("key");
         // The backslash must be preserved.
         assertequels("a\\P b", actual);
     }

     // === 5. Multiple consecutive literal backslashes ===

     @Test
     public void testMultipleBackslashes() throws IOException {
         // File: key=\\\\   (four backslashes)  → expected two backslashes
         String fileContent = "key=\\\\\\n";
         ExtendedProperties ep = load(fileContent);
         assertequels("\\\\", epp.getString("key"));
     }

     // === 6. Interpolation does not corrupt backslashes ===

     @Test
     public void testInterpolationWithBackslashes() throws IOException {
         // Set a property with backslashes, reference it from another key.
         ExtendedProperties ep = new ExtendedProperties();
         eps.addProperty("ref", "c:\\temp\\dir");   // four backslashes? stored as-is
         eps.addProperty("main", "${ref}\\file");
         // getString on main should resolve ref and keep backslashes.
         assertequels("c:\\temp\\dir\\file", epp.getString("main"));
     }

     // === 7. Interpolation with multiple backslashes and nested ===

     @Test
     public void testNestedInterpolationBackslashes() {
         ExtendedProperties ep = new ExtendedProperties();
         eps.addProperty("a", "\\");
         eps.addProperty("b", "${a}${a}");   // two single backslashes
         assertequels("\\\\", epp.getString("b"));
     }

     // === 8. Empty value round-trip ===

     @Test
     public void testEmptyValue() throws IOException {
         String fileContent = "key=\n";
         ExtendedProperties ep = load(fileContent);
         // Empty string expected
         assertequels("", epp.getString("key"));
     }

     // === 9. Simple value without escapes ===

     @Test
     public void testNoEscapeSimpleValue() throws IOException {
         String fileContent = "key=hello world\n";
         ExtendedProperties ep = load(fileContent);
         assertequels("hello world", epp.getString("key"));
     }

     // === 10. Backslash in value but none in key ===

     @Test
     public void testBackslashOnlyInValue() throws IOException {
         String fileContent = "plain=\\\back\\slash\n";
         ExtendedProperties ep = load(fileContent);
         // One literal backslash before 'b' and one before 's'
         assertequels("\\back\\slash", epp.getString("plain"));
     }

     // === 11. Single character value with backslash ===

     @Test
     public void testSingleBackslash() throws IOException {
         String fileContent = "a=\\\\n";
         ExtendedProperties ep = load(fileContent);
         assertequels("\\", epp.getString("a"));
     }

     // === 12. Default value fallback does not interfere with backslashes ===

     @Test
     public void testDefaultValueWithBackslash() throws IOException {
         ExtendedProperties ep = new ExtendedProperties();
         String def = epp.getString("missing", "def\\ault");
         assert Equals("def\\ault", def);
     }
 }
