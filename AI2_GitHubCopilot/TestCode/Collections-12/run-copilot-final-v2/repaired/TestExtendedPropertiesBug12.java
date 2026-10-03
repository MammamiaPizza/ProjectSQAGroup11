package org.apache.commons.collections;

 import static org.junit.Assert.*;

 import java.io.*;
 import java.util.*;

 import org.junit.*;

 /**
  * Tests targeting the bug described in COLLECTIONS-271
  * and related interpolation / escaping behaviours.
  */
 public class TestExtendedPropertiesBug12 {

     private File tempDir;

     @Before
     public void setUp() throws IOException {
         tempDir = createTempDirectory();
     }

     @After
     public void tearDown() {
         deleteDirectory(tempDir);
     }

     // Helper to write a temporary .properties file.
     private File writeFile(String name, String content) throws IOException {
         File file = new File(tempDir, name);
         PrintWriter pw = new PrintWriter(new FileWriter(file));
         pw.print(content);
         pw.close();
         return file;
     }

     private static File createTempDirectory() throws IOException {
         File dir = File.createTempFile("extprop", ".tmp");
         dir.delete();
         dir.mkdir();
         return dir;
     }

     private static void deleteDirectory(File dir) {
         if (dir.isDirectory()) {
             for (File f : dir.listFiles()) {
                 deleteDirectory(f);
             }
         }
         dir.delete();
     }

     // ---------------------------------------------------------------
     // COLLECTIONS-271: repeated undefined variable falsely triggers
     // infinite loop detection
     // ---------------------------------------------------------------
     @Test
     public void testMultipleUndefinedSameTokenIsNotACycle() {
         ExtendedProperties props = new ExtendedProperties();
         props.setProperty("key", "${missing}/${missing}");
         String result = props.getString("key");        assertEquals("${missing}/${missing}",
result);
     }

     // ---------------------------------------------------------------
     // Self-referential property must throw IllegalStateException
     // ---------------------------------------------------------------
     @Test(expected = IllegalStateException.class)
     public void testSelfReferenceThrowsLoopException() {
         ExtendedProperties props = new ExtendedProperties();
         props.setProperty("loop", "${loop}");
         props.getString("loop"); // should throw
     }

     // ---------------------------------------------------------------
     // Two-key cycle must also throw
     // ---------------------------------------------------------------
     @Test(expected = IllegalStateException.class)
     public void testTwoKeyCycleThrowsLoopException() {
         ExtendedProperties props = new ExtendedProperties();
         props.setProperty("a", "${b}");
         props.setProperty("b", "${a}");
         props.getString("a"); // should throw
     }

     // ---------------------------------------------------------------
     // Normal interpolation
     // ---------------------------------------------------------------
     @Test
     public void testSimpleInterpolation() {
         ExtendedProperties props = new ExtendedProperties();
         props.setProperty("greeting", "hello");
         props.setProperty("msg", "${greeting} world");
         assertEquals("hello world", props.getString("msg"));
     }

     // ---------------------------------------------------------------
     // Interpolation with defaults (single level)
     // ---------------------------------------------------------------
     @Test
     public void testInterpolationFromDefaultsSingleLevel() throws Exception {
         File mainFile = writeFile("main.properties", "a = ${b}\n");
         File defaultFile = writeFile("default.properties", "b = world\n");
         ExtendedProperties props = new ExtendedProperties(
                 mainFile.getAbsolutePath(),
                 defaultFile.getAbsolutePath());
         assertEquals("world", props.getString("a"));
     }

     // ---------------------------------------------------------------
     // Interpolation with defaults (two levels – COLLECTIONS-271 chain)
     // ---------------------------------------------------------------
     @Test
     public void testInterpolationFromDefaultsChained() throws Exception {
         File mainFile = writeFile("main.properties", "a = ${b}\n");
         File defaultFile = writeFile("default.properties",
                 "b = ${c}\nc = world\n");
         ExtendedProperties props = new ExtendedProperties(
                 mainFile.getAbsolutePath(),
                 defaultFile.getAbsolutePath());
         assertEquals("world", props.getString("a"));
     }

     // ---------------------------------------------------------------
     // Missing variable kept as-is
     // ---------------------------------------------------------------
     @Test
     public void testMissingVariableLeftUnresolved() {
         ExtendedProperties props = new ExtendedProperties();
         props.setProperty("item", "${noSuch}");
         assertEquals("${noSuch}", props.getString("item"));
     }

     // ---------------------------------------------------------------
     // getStringArray basics
     // ---------------------------------------------------------------
     @Test
     public void testGetStringArraySimple() {
         ExtendedProperties props = new ExtendedProperties();
         props.setProperty("list", "a,b,c");
         assertArrayEquals(new String[]{"a", "b", "c"}, props.getStringArray("list"));
     }

     // ---------------------------------------------------------------
     // getStringArray with escaped comma (via escape/unescape)
     // ---------------------------------------------------------------
     @Test
     public void testGetStringArrayEscapedComma() {
         ExtendedProperties props = new ExtendedProperties();
         props.addProperty("escaped", "a\\,b");  // literal backslash-comma
         String[] arr = props.getStringArray("escaped");
         // After unescaping the value is "a,b" as one element
         assertArrayEquals(new String[]{"a,b"}, arr);
     }

     // ---------------------------------------------------------------
     // Boolean parsing
     // ---------------------------------------------------------------
     @Test
     public void testGetBoolean() {
         ExtendedProperties props = new ExtendedProperties();
         props.setProperty("b1", "true");
         props.setProperty("b0", "false");
         props.setProperty("b2", "yes");
         assertTrue(props.getBoolean("b1"));
         assertFalse(props.getBoolean("b0"));
         // "yes" should be false according to testBoolean default?
         // public contract: testBoolean returns Boolean.TRUE only for "true",
         // everything else is Boolean.FALSE.
         assertFalse(props.getBoolean("b2"));
     }

     // ---------------------------------------------------------------
     // Combine two ExtendedProperties
     // ---------------------------------------------------------------
     @Test
     public void testCombine() {
         ExtendedProperties base = new ExtendedProperties();
         base.setProperty("a", "1");        ExtendedProperties other = new ExtendedProperties();
         other.setProperty("b", "2");        base.combine(other);
         assertEquals("1", base.getString("a"));        assertEquals("2", base.getString("b"));    }

     // ---------------------------------------------------------------
     // Subset (prefix selection)
     // ---------------------------------------------------------------
     @Test
     public void testSubset() {
         ExtendedProperties props = new ExtendedProperties();
         props.setProperty("server.name", "main");
         props.setProperty("server.port", "8080");
         props.setProperty("client.name", "app");
         ExtendedProperties subset = props.subset("server");
         assertEquals(2, subset.size());
         assertEquals("main", subset.getString("name"));
         assertEquals("8080", subset.getString("port"));
     }

     // ---------------------------------------------------------------
     // Clear property
     // ---------------------------------------------------------------
     @Test
     public void testClearProperty() {
         ExtendedProperties props = new ExtendedProperties();
         props.setProperty("x", "1");
         assertNotNull(props.getProperty("x"));
         props.clearProperty("x");
         assertNull(props.getString("x")); // getString returns null for missing
     }

     // ---------------------------------------------------------------
     // load + save roundtrip
     // ---------------------------------------------------------------
     @Test
     public void testLoadSaveRoundtrip() throws Exception {
         File orig = writeFile("orig.properties", "key = value\n");
         ExtendedProperties props = new ExtendedProperties(orig.getAbsolutePath());
         assertEquals("value", props.getString("key"));
         File saved = new File(tempDir, "saved.properties");
         OutputStream fos = new FileOutputStream(saved);
         props.save(fos, "header");
         fos.close();
         ExtendedProperties reloaded = new ExtendedProperties(saved.getAbsolutePath());
         assertEquals("value", reloaded.getString("key"));
     }

 }
