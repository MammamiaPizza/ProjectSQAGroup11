package com.fasterxml.jackson.databind.util;

 import static org.junit.Assert.*;

 import java.io.IOException;

 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.JsonToken;

 public class TokenBufferTest {

     @Test
     public void testEmptyObject() throws IOException {
         TokenBuffer tb = new TokenBuffer(null, false);
         tb.writeStartObject();
         tb.writeEndObject();
         tb.close();

         JsonParser p = tb.asParser();
         assertEquals(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(JsonToken.END_OBJECT, p.nextToken());
         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testObjectWithOneField() throws IOException {
         TokenBuffer tb = new TokenBuffer(null, false);
         tb.writeStartObject();
         tb.writeFieldName("key");
         tb.writeString("value");
         tb.writeEndObject();
         tb.close();

         JsonParser p = tb.asParser();
         assertEquals("First token must be START_OBJECT", JsonToken.START_OBJECT, p.nextToken());
         assertEquals("Second token must be FIELD_NAME", JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("key", p.getCurrentName());
         assertEquals("Third token must be VALUE_STRING", JsonToken.VALUE_STRING, p.nextToken());
         assertEquals("value", p.getText());
         assertEquals("Fourth token must be END_OBJECT", JsonToken.END_OBJECT, p.nextToken());
         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testNestedObject() throws IOException {
         TokenBuffer tb = new TokenBuffer(null, false);
         tb.writeStartObject();
         tb.writeFieldName("outer");
         tb.writeStartObject();
         tb.writeFieldName("inner");
         tb.writeNumber(42);
         tb.writeEndObject();
         tb.writeEndObject();
         tb.close();

         JsonParser p = tb.asParser();
         assertEquals(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("outer", p.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("inner", p.getCurrentName());
         assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(42, p.getIntValue());
         assertEquals(JsonToken.END_OBJECT, p.nextToken());
         assertEquals(JsonToken.END_OBJECT, p.nextToken());
         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testArrayInObject() throws IOException {
         TokenBuffer tb = new TokenBuffer(null, false);
         tb.writeStartObject();
         tb.writeFieldName("items");
         tb.writeStartArray();
         tb.writeString("a");
         tb.writeNumber(1);
         tb.writeEndArray();
         tb.writeEndObject();
         tb.close();

         JsonParser p = tb.asParser();
         assertEquals(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("items", p.getCurrentName());
         assertEquals(JsonToken.START_ARRAY, p.nextToken());
         assertEquals(JsonToken.VALUE_STRING, p.nextToken());
         assertEquals("a", p.getText());
         assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(1, p.getIntValue());
         assertEquals(JsonToken.END_ARRAY, p.nextToken());
         assertEquals(JsonToken.END_OBJECT, p.nextToken());
         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testBug592FieldNameWithoutStartObject() throws IOException {
         // Bug #592: copyCurrentStructure called with parser at FIELD_NAME writes
         // FIELD_NAME + VALUE tokens but omits START_OBJECT. asParser() must
         // recognize this and yield START_OBJECT as the first token.
         TokenBuffer tb = new TokenBuffer(null, false);
         tb.writeFieldName("delegatedField");
         tb.writeString("delegatedValue");
         tb.close();

         JsonParser p = tb.asParser();
         JsonToken first = p.nextToken();
         assertNotNull("First token should not be null", first);
         assertEquals("Bug #592: first token after writeFieldName without START_OBJECT must be
START_OBJECT",
                 JsonToken.START_OBJECT, first);
         p.close();
     }

     @Test
     public void testBug592MultipleFieldsWithoutStartObject() throws IOException {
         // Variant of bug #592: multiple field-name+value pairs written without
         // explicit START_OBJECT. asParser() must yield START_OBJECT first,
         // then all field/value pairs in order.
         TokenBuffer tb = new TokenBuffer(null, false);
         tb.writeFieldName("x");
         tb.writeNumber(1);
         tb.writeFieldName("y");
         tb.writeString("hello");
         tb.close();

         JsonParser p = tb.asParser();
         assertEquals("Bug #592: first token must be START_OBJECT",
                 JsonToken.START_OBJECT, p.nextToken());
         assertEquals(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("x", p.getCurrentName());
         assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(1, p.getIntValue());
         assertEquals(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("y", p.getCurrentName());
         assertEquals(JsonToken.VALUE_STRING, p.nextToken());
         assertEquals("hello", p.getText());
         p.close();
     }

     @Test
     public void testFirstToken() throws IOException {
         TokenBuffer tb = new TokenBuffer(null, false);
         tb.writeStartObject();
         tb.writeFieldName("a");
         tb.writeNumber(1);
         tb.writeEndObject();
         tb.close();

         assertEquals("firstToken() must return START_OBJECT",
                 JsonToken.START_OBJECT, tb.firstToken());
     }

     @Test
     public void testAppendTwoBuffers() throws IOException {
         TokenBuffer tb1 = new TokenBuffer(null, false);
         tb1.writeStartObject();
         tb1.writeFieldName("a");
         tb1.writeNumber(1);
         tb1.writeEndObject();

         TokenBuffer tb2 = new TokenBuffer(null, false);
         tb2.writeStartObject();
         tb2.writeFieldName("b");
         tb2.writeString("val");
         tb2.writeEndObject();

         tb1.append(tb2);
         tb1.close();

         JsonParser p = tb1.asParser();
         assertEquals(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("a", p.getCurrentName());
         assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(1, p.getIntValue());
         assertEquals(JsonToken.END_OBJECT, p.nextToken());

         assertEquals(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("b", p.getCurrentName());
         assertEquals(JsonToken.VALUE_STRING, p.nextToken());
         assertEquals("val", p.getText());
         assertEquals(JsonToken.END_OBJECT, p.nextToken());
         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testArrayTokens() throws IOException {
         TokenBuffer tb = new TokenBuffer(null, false);
         tb.writeStartArray();
         tb.writeString("first");
         tb.writeNumber(2);
         tb.writeBoolean(true);
         tb.writeNull();
         tb.writeEndArray();
         tb.close();

         JsonParser p = tb.asParser();
         assertEquals(JsonToken.START_ARRAY, p.nextToken());
         assertEquals(JsonToken.VALUE_STRING, p.nextToken());
         assertEquals("first", p.getText());
         assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(2, p.getIntValue());
         assertEquals(JsonToken.VALUE_TRUE, p.nextToken());
         assertEquals(JsonToken.VALUE_NULL, p.nextToken());
         assertEquals(JsonToken.END_ARRAY, p.nextToken());
         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testCloseState() throws IOException {
         TokenBuffer tb = new TokenBuffer(null, false);
         assertFalse("Buffer should not be closed initially", tb.isClosed());
         tb.close();
         assertTrue("Buffer must be closed after close()", tb.isClosed());
     }

     @Test
     public void testParserOnClosedBuffer() throws IOException {
         TokenBuffer tb = new TokenBuffer(null, false);
         tb.writeStartObject();
         tb.writeEndObject();
         tb.close();

         assertTrue(tb.isClosed());
         JsonParser p = tb.asParser();
         assertNotNull("asParser() must return a parser even on a closed buffer", p);
         assertEquals(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(JsonToken.END_OBJECT, p.nextToken());
         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testMultipleFieldsObject() throws IOException {
         TokenBuffer tb = new TokenBuffer(null, false);
         tb.writeStartObject();
         tb.writeFieldName("name");
         tb.writeString("test");
         tb.writeFieldName("count");
         tb.writeNumber(42);
         tb.writeFieldName("active");
         tb.writeBoolean(true);
         tb.writeFieldName("ratio");
         tb.writeNumber(3.14);
         tb.writeEndObject();
         tb.close();

         JsonParser p = tb.asParser();
         assertEquals(JsonToken.START_OBJECT, p.nextToken());

         assertEquals(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("name", p.getCurrentName());
         assertEquals(JsonToken.VALUE_STRING, p.nextToken());
         assertEquals("test", p.getText());

         assertEquals(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("count", p.getCurrentName());
         assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(42, p.getIntValue());

         assertEquals(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("active", p.getCurrentName());
         assertEquals(JsonToken.VALUE_TRUE, p.nextToken());

         assertEquals(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("ratio", p.getCurrentName());
         assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
         assertEquals(3.14, p.getDoubleValue(), 0.001);

         assertEquals(JsonToken.END_OBJECT, p.nextToken());
         assertNull(p.nextToken());
         p.close();
     }
 }
