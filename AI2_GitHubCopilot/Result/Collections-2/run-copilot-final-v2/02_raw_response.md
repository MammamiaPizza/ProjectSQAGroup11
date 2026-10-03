package org.apache.commons.collections;

 import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStream;

 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;

 /**
  * Tests for COLLECTIONS-214: ExtendedProperties.load(InputStream)
  * incorrectly overwrites the static "include" field when a loaded
  * property key matches the current include property name.
  */
 public class TestExtendedPropertiesInclude {

     /** Fresh instance used to reset the static include field between tests. */
     private ExtendedProperties resetter;

     @Before
     public void setUp() {
         resetter = new ExtendedProperties();
         resetter.setInclude("include");
     }

     @After
     public void tearDown() {
         resetter.setInclude("include");
     }

     @Test
     public void testDefaultInclude() {
         assertEquals("include", new ExtendedProperties().getInclude());
     }

     @Test
     public void testLoadDoesNotOverwriteInclude() throws IOException {
         String data = "include = import\nother = value\n";
         InputStream in = new ByteArrayInputStream(data.getBytes("ISO-8859-1"));
         ExtendedProperties props = new ExtendedProperties();
         props.load(in);
         assertEquals("include", props.getInclude());
     }

     @Test
     public void testSetInclude() {
         ExtendedProperties props = new ExtendedProperties();
         props.setInclude("import");
         assertEquals("import", props.getInclude());
     }

     @Test
     public void testIncludeIsStatic() {
         ExtendedProperties a = new ExtendedProperties();
         ExtendedProperties b = new ExtendedProperties();
         assertEquals(a.getInclude(), b.getInclude());
         a.setInclude("changed");
         assertEquals("changed", b.getInclude());
     }

     @Test
     public void testLoadPreservesPropertyValue() throws IOException {
         String data = "include = targetFile.properties\n";
         InputStream in = new ByteArrayInputStream(data.getBytes("ISO-8859-1"));
         ExtendedProperties props = new ExtendedProperties();
         props.load(in);
         assertEquals("targetFile.properties", props.getProperty("include"));
         assertEquals("include", props.getInclude());
     }

     @Test
     public void testMultipleLoadsDoNotChangeInclude() throws IOException {
         ExtendedProperties props = new ExtendedProperties();
         props.load(new ByteArrayInputStream("include = first\n".getBytes("ISO-8859-1")));
         assertEquals("include", props.getInclude());
         props.load(new ByteArrayInputStream("include = second\n".getBytes("ISO-8859-1")));
         assertEquals("include", props.getInclude());
     }

     @Test
     public void testLoadSubstringKeys() throws IOException {
         String data = "includes = yes\nincluded = true\n";
         InputStream in = new ByteArrayInputStream(data.getBytes("ISO-8859-1"));
         ExtendedProperties props = new ExtendedProperties();
         props.load(in);
         assertEquals("include", props.getInclude());
     }

     @Test
     public void testEmptyLoad() throws IOException {
         ExtendedProperties props = new ExtendedProperties();
         props.load(new ByteArrayInputStream(new byte[0]));
         assertEquals("include", props.getInclude());
     }

     @Test
     public void testLoadAfterChangingIncludeKey() throws IOException {
         new ExtendedProperties().setInclude("import");
         String data = "import = someFile.properties\n";
         InputStream in = new ByteArrayInputStream(data.getBytes("ISO-8859-1"));
         ExtendedProperties props = new ExtendedProperties();
         props.load(in);
         assertEquals("import", props.getInclude());
     }

     @Test
     public void testLoadWithEncoding() throws IOException {
         String data = "include = val\nkey = val2\n";
         InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));
         ExtendedProperties props = new ExtendedProperties();
         props.load(in, "UTF-8");
         assertEquals("include", props.getInclude());
     }

     @Test
     public void testConstructorLoad() throws IOException {
         File temp = File.createTempFile("test-ep-", ".properties");
         temp.deleteOnExit();
         FileWriter w = new FileWriter(temp);
         w.write("include = import\nother = value\n");
         w.close();
         try {
             ExtendedProperties props = new ExtendedProperties(temp.getAbsolutePath());
             assertEquals("include", props.getInclude());
         } finally {
             temp.delete();
         }
     }

     @Test
     public void testGetIncludeConsistency() {
         ExtendedProperties a = new ExtendedProperties();
         ExtendedProperties b = new ExtendedProperties();
         assertEquals(a.getInclude(), b.getInclude());
         a.setInclude("test");
         assertEquals("test", b.getInclude());
         assertEquals(a.getInclude(), b.getInclude());
     }
 }