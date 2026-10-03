package com.fasterxml.jackson.core.json;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 import java.io.IOException;
 import java.io.StringWriter;

 import org.junit.Test;

 import com.fasterxml.jackson.core.*;

 /**
  * Tests for {@link JsonGeneratorImpl} focusing on the
  * {@link Feature#QUOTE_FIELD_NAMES} behavior for JSON Object property names.
  *
  * Bug report: 246 – When QUOTE_FIELD_NAMES is disabled, the generator
  * incorrectly still quotes field names that contain no special characters.
  */
 public class TestJsonGeneratorFeatures {

     private JsonFactory newFactory() {
         return new JsonFactory();
     }

     private JsonGenerator createGenerator(StringWriter sw) throws IOException {
         return newFactory().createGenerator(sw);
     }

     // ----------------------------------------------------------------------
     // Fault-related tests: QUOTE_FIELD_NAMES disabled → unquoted names
     // ----------------------------------------------------------------------

     /**
      * Core regression: simple field name with QUOTE_FIELD_NAMES disabled
      * should produce <code>{"foo":1}</code> without quotes around "foo".
      */
     @Test
     public void testSimpleFieldNameUnquoted() throws IOException {
         StringWriter sw = new StringWriter();
         JsonGenerator g = createGenerator(sw);
         g.disable(Feature.QUOTE_FIELD_NAMES);
         g.writeStartObject();
         g.writeNumberField("foo", 1);
         g.writeEndObject();
         g.close();
         assertEquals("{\"foo\":1}", sw.toString());
     }

     /**
      * Default behavior: QUOTE_FIELD_NAMES is enabled by default,
      * so field names must be quoted.
      */
     @Test
     public void testSimpleFieldNameQuotedDefault() throws IOException {
         StringWriter sw = new StringWriter();
         JsonGenerator g = createGenerator(sw);
         g.writeStartObject();
         g.writeNumberField("bar", 2);
         g.writeEndObject();
         g.close();
         assertEquals("{\"bar\":2}", sw.toString());
     }

     /**
      * Enabling QUOTE_FIELD_NAMES explicitly must quote the field name.
      */
     @Test
     public void testSimpleFieldNameExplicitlyQuoted() throws IOException {
         StringWriter sw = new StringWriter();
         JsonGenerator g = createGenerator(sw);
         g.enable(Feature.QUOTE_FIELD_NAMES);
         g.writeStartObject();
         g.writeNumberField("baz", 3);
         g.writeEndObject();
         g.close();
         assertEquals("{\"baz\":3}", sw.toString());
     }

     /**
      * Disabling QUOTE_FIELD_NAMES via disable() and then enabling it again
      * should restore quoted output.
      */
     @Test
     public void testToggleFieldNameQuoting() throws IOException {
         StringWriter sw = new StringWriter();
         JsonGenerator g = createGenerator(sw);
         g.disable(Feature.QUOTE_FIELD_NAMES);
         g.enable(Feature.QUOTE_FIELD_NAMES);
         g.writeStartObject();
         g.writeNumberField("toggle", 42);
         g.writeEndObject();
         g.close();
         assertEquals("{\"toggle\":42}", sw.toString());
     }

     /**
      * A field name containing a space must still be quoted even when
      * QUOTE_FIELD_NAMES is disabled, because unquoted JSON names
      * cannot contain whitespace.
      */
     @Test
     public void testFieldNameWithSpaceForcedQuoted() throws IOException {
         StringWriter sw = new StringWriter();
         JsonGenerator g = createGenerator(sw);
         g.disable(Feature.QUOTE_FIELD_NAMES);
         g.writeStartObject();
         g.writeNumberField("foo bar", 10);
         g.writeEndObject();
         g.close();
         // Must be quoted despite QUOTE_FIELD_NAMES=disabled
         assertEquals("{\"foo bar\":10}", sw.toString());
     }

     /**
      * Multiple fields with QUOTE_FIELD_NAMES disabled: all simple names
      * must be unquoted.
      */
     @Test
     public void testMultipleFieldsUnquoted() throws IOException {
         StringWriter sw = new StringWriter();
         JsonGenerator g = createGenerator(sw);
         g.disable(Feature.QUOTE_FIELD_NAMES);
         g.writeStartObject();
         g.writeNumberField("a", 1);
         g.writeNumberField("b", 2);
         g.writeNumberField("c", 3);
         g.writeEndObject();
         g.close();
         assertEquals("{\"a\":1,\"b\":2,\"c\":3}", sw.toString());
     }

     /**
      * Mixed: simple field name (unquoted) plus field name with special
      * characters (must remain quoted).
      */
     @Test
     public void testMixedQuoting() throws IOException {
         StringWriter sw = new StringWriter();
         JsonGenerator g = createGenerator(sw);
         g.disable(Feature.QUOTE_FIELD_NAMES);
         g.writeStartObject();
         g.writeNumberField("simple", 1);
         g.writeNumberField("has space", 2);
         g.writeNumberField("alsoSimple", 3);
         g.writeEndObject();
         g.close();
         assertEquals("{\"simple\":1,\"has space\":2,\"alsoSimple\":3}", sw.toString());
     }

     // ----------------------------------------------------------------------
     // Normal / boundary tests
     // ----------------------------------------------------------------------

     /**
      * Empty field name with QUOTE_FIELD_NAMES disabled.
      * An empty string is a valid JSON name, but it must be quoted
      * because an unquoted empty name is not valid JSON.
      */
     @Test
     public void testEmptyFieldNameUnquotedDisabled() throws IOException {
         StringWriter sw = new StringWriter();
         JsonGenerator g = createGenerator(sw);
         g.disable(Feature.QUOTE_FIELD_NAMES);
         g.writeStartObject();
         g.writeNumberField("", 0);
         g.writeEndObject();
         g.close();
         // Empty name must still be quoted in valid JSON
         assertEquals("{\"\":0}", sw.toString());
     }

     /**
      * writeStringField (not writeNumberField) with QUOTE_FIELD_NAMES disabled.
      */
     @Test
     public void testWriteStringFieldUnquoted() throws IOException {
         StringWriter sw = new StringWriter();
         JsonGenerator g = createGenerator(sw);
         g.disable(Feature.QUOTE_FIELD_NAMES);
         g.writeStartObject();
         g.writeStringField("key", "value");
         g.writeEndObject();
         g.close();
         assertEquals("{\"key\":\"value\"}", sw.toString());
     }

     /**
      * writeStringField with QUOTE_FIELD_NAMES enabled (default).
      */
     @Test
     public void testWriteStringFieldQuoted() throws IOException {
         StringWriter sw = new StringWriter();
         JsonGenerator g = createGenerator(sw);
         g.writeStartObject();
         g.writeStringField("key", "value");
         g.writeEndObject();
         g.close();
         assertEquals("{\"key\":\"value\"}", sw.toString());
     }

     /**
      * Field name starting with a digit must still be quoted even
      * when QUOTE_FIELD_NAMES is disabled because unquoted JSON
      * names cannot start with a digit.
      */
     @Test
     public void testFieldNameStartingWithDigitForcedQuoted() throws IOException {
         StringWriter sw = new StringWriter();
         JsonGenerator g = createGenerator(sw);
         g.disable(Feature.QUOTE_FIELD_NAMES);
         g.writeStartObject();
         g.writeNumberField("123abc", 99);
         g.writeEndObject();
         g.close();
         assertEquals("{\"123abc\":99}", sw.toString());
     }
 }
