package com.google.gson.stream;

 import com.google.gson.internal.bind.JsonTreeWriter;
 import com.google.gson.internal.bind.TypeAdapters;
 import java.io.IOException;
 import java.io.StringWriter;
 import junit.framework.TestCase;

 /**
  * Tests that expose bug #836 (NullPointerException when writing a null boxed Boolean).
  * The buggy version lacks a value(Boolean) overload and relies on auto-unboxing to
  * value(boolean), which throws NPE for null.
  */
 public class JsonWriterTest extends TestCase {

     private StringWriter stringWriter;
     private JsonWriter jsonWriter;

     @Override
     protected void setUp() {
         stringWriter = new StringWriter();
         jsonWriter = new JsonWriter(stringWriter);
     }

     public void testBoxedBooleanTrue() throws IOException {
         jsonWriter.value(Boolean.TRUE);
         jsonWriter.close();
         assertEquals("true", stringWriter.toString());
     }

     public void testBoxedBooleanFalse() throws IOException {
         jsonWriter.value(Boolean.FALSE);
         jsonWriter.close();
         assertEquals("false", stringWriter.toString());
     }

     public void testNullBoxedBoolean() throws IOException {
         jsonWriter.setSerializeNulls(true);
         jsonWriter.value((Boolean) null);
         jsonWriter.close();
         assertEquals("null", stringWriter.toString());
     }

     public void testNullBoxedBooleanWithoutSerializeNulls() throws IOException {
         jsonWriter.setSerializeNulls(false);
         jsonWriter.value((Boolean) null);
         jsonWriter.close();
         assertEquals("", stringWriter.toString());
     }

     public void testNullBoxedBooleanInArray() throws IOException {
         jsonWriter.setSerializeNulls(true);
         jsonWriter.beginArray().value((Boolean) null).endArray();
         jsonWriter.close();
         assertEquals("[null]", stringWriter.toString());
     }

     public void testNullBoxedBooleanAsNamedValue() throws IOException {
         jsonWriter.setSerializeNulls(true);
         jsonWriter.beginObject().name("key").value((Boolean) null).endObject();
         jsonWriter.close();
         assertEquals("{\"key\":null}", stringWriter.toString());
     }

     public void testTypeAdapterBooleanWriteNull() throws IOException {
         jsonWriter.setSerializeNulls(true);
         TypeAdapters.BOOLEAN.write(jsonWriter, (Boolean) null);
         jsonWriter.close();
         assertEquals("null", stringWriter.toString());
     }

     public void testTypeAdapterBooleanWriteTrue() throws IOException {
         TypeAdapters.BOOLEAN.write(jsonWriter, true);
         jsonWriter.close();
         assertEquals("true", stringWriter.toString());
     }

     public void testTypeAdapterBooleanWriteFalse() throws IOException {
         TypeAdapters.BOOLEAN.write(jsonWriter, false);
         jsonWriter.close();
         assertEquals("false", stringWriter.toString());
     }

     public void testValueNullNumberDoesNotNPE() throws IOException {
         jsonWriter.value((Number) null);
         jsonWriter.close();
         assertEquals("null", stringWriter.toString());
     }

     public void testJsonTreeWriterBoxedBooleanNull() {
         JsonTreeWriter treeWriter = new JsonTreeWriter();
         try {
             treeWriter.value((Boolean) null);
             fail("Should have thrown an IOException or NPE? Actually expected no exception");
         } catch (IOException e) {
             throw new AssertionError("Unexpected IOException", e);
         } catch (NullPointerException expected) {
         }
     }

     public void testJsonTreeWriterBoxedBooleanNullShouldNotThrow() throws IOException {
         JsonTreeWriter treeWriter = new JsonTreeWriter();
         treeWriter.value((Boolean) null);
         assertNotNull(treeWriter.get());
     }
 }
