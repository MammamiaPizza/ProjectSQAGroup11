package com.fasterxml.jackson.core.failing;

 import static org.junit.Assert.*;

 import java.io.IOException;

 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonFactory;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.JsonToken;

 public class Bug9Test {

     private void assertToken(JsonToken expected, JsonToken actual) {
         assertEquals("Expected token " + expected + ", but got " + actual, expected, actual);
     }

     // Bytes

     @Test
     public void testFieldNameBytes() throws Exception {
         JsonFactory f = new JsonFactory();
         byte[] data = "{\"a\":1, \"b\":2}".getBytes("UTF-8");
         JsonParser p = f.createParser(data);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("a", p.getValueAsString());
             // skip value
             p.nextToken();
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("b", p.getValueAsString());
         } finally {
             p.close();
         }
     }

     @Test
     public void testFieldNameChars() throws Exception {
         JsonFactory f = new JsonFactory();
         String json = "{\"a\":1, \"b\":2}";
         JsonParser p = f.createParser(json);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("a", p.getValueAsString());
             p.nextToken(); // skip value
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("b", p.getValueAsString());
         } finally {
             p.close();
         }
     }

     @Test
     public void testStringValueBytes() throws Exception {
         JsonFactory f = new JsonFactory();
         byte[] data = "{\"x\":\"hello\"}".getBytes("UTF-8");
         JsonParser p = f.createParser(data);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertToken(JsonToken.VALUE_STRING, p.nextToken());
             assertEquals("hello", p.getValueAsString());
         } finally {
             p.close();
         }
     }

     @Test
     public void testStringValueChars() throws Exception {
         JsonFactory f = new JsonFactory();
         String json = "{\"x\":\"hello\"}";
         JsonParser p = f.createParser(json);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertToken(JsonToken.VALUE_STRING, p.nextToken());
             assertEquals("hello", p.getValueAsString());
         } finally {
             p.close();
         }
     }

     @Test
     public void testEmptyFieldNameBytes() throws Exception {
         JsonFactory f = new JsonFactory();
         byte[] data = "{\"\":1}".getBytes("UTF-8");
         JsonParser p = f.createParser(data);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("", p.getValueAsString());
         } finally {
             p.close();
         }
     }

     @Test
     public void testEmptyFieldNameChars() throws Exception {
         JsonFactory f = new JsonFactory();
         String json = "{\"\":1}";
         JsonParser p = f.createParser(json);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("", p.getValueAsString());
         } finally {
             p.close();
         }
     }

     @Test
     public void testEscapedFieldNameBytes() throws Exception {
         JsonFactory f = new JsonFactory();
         byte[] data = "{\"f\\\"oo\":1}".getBytes("UTF-8");
         JsonParser p = f.createParser(data);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("f\"oo", p.getValueAsString());
         } finally {
             p.close();
         }
     }

     @Test
     public void testEscapedFieldNameChars() throws Exception {
         JsonFactory f = new JsonFactory();
         String json = "{\"f\\\"oo\":1}";
         JsonParser p = f.createParser(json);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("f\"oo", p.getValueAsString());
         } finally {
             p.close();
         }
     }

     @Test
     public void testUnicodeFieldNameBytes() throws Exception {
         JsonFactory f = new JsonFactory();
         byte[] data = "{\"\\u0041\":1}".getBytes("UTF-8");
         JsonParser p = f.createParser(data);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("A", p.getValueAsString());
         } finally {
             p.close();
         }
     }

     @Test
     public void testUnicodeFieldNameChars() throws Exception {
         JsonFactory f = new JsonFactory();
         String json = "{\"\\u0041\":1}";
         JsonParser p = f.createParser(json);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("A", p.getValueAsString());
         } finally {
             p.close();
         }
     }

     @Test
     public void testOtherTokenTypesBytes() throws Exception {
         JsonFactory f = new JsonFactory();
         byte[] data = "{\"b\":true, \"n\":null, \"i\":123}".getBytes("UTF-8");
         JsonParser p = f.createParser(data);
         try {
             // non-scalar START_OBJECT returns null
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             assertNull(p.getValueAsString());

             // boolean
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("b", p.getValueAsString());
             assertToken(JsonToken.VALUE_TRUE, p.nextToken());
             assertEquals("true", p.getValueAsString());

             // null
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("n", p.getValueAsString());
             assertToken(JsonToken.VALUE_NULL, p.nextToken());
             assertNull(p.getValueAsString());

             // number
             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("i", p.getValueAsString());
             assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
             assertEquals("123", p.getValueAsString());

             // end object
             assertToken(JsonToken.END_OBJECT, p.nextToken());
             assertNull(p.getValueAsString());
         } finally {
             p.close();
         }
     }

     @Test
     public void testOtherTokenTypesChars() throws Exception {
         JsonFactory f = new JsonFactory();
         String json = "{\"b\":true, \"n\":null, \"i\":123}";
         JsonParser p = f.createParser(json);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             assertNull(p.getValueAsString());

             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("b", p.getValueAsString());
             assertToken(JsonToken.VALUE_TRUE, p.nextToken());
             assertEquals("true", p.getValueAsString());

             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("n", p.getValueAsString());
             assertToken(JsonToken.VALUE_NULL, p.nextToken());
             assertNull(p.getValueAsString());

             assertToken(JsonToken.FIELD_NAME, p.nextToken());
             assertEquals("i", p.getValueAsString());
             assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
             assertEquals("123", p.getValueAsString());

             assertToken(JsonToken.END_OBJECT, p.nextToken());
             assertNull(p.getValueAsString());
         } finally {
             p.close();
         }
     }

@Test
    public void testNextFieldNameGetValueAsStringBytes() throws Exception {
        JsonFactory f = new JsonFactory();
        JsonParser p = f.createParser(new byte[] { '{', '"', 'a', '"', ':', '1', '}' });
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        String fieldName = p.nextFieldName();
        assertEquals("a", fieldName);
        assertEquals("a", p.getValueAsString());
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        p.close();
    }

 @Test
 public void testNextFieldNameGetValueAsStringChars() throws Exception {
     JsonFactory f = new JsonFactory();
     JsonParser p = f.createParser(new java.io.StringReader("{\"a\":1}"));
     assertToken(JsonToken.START_OBJECT, p.nextToken());
     String fieldName = p.nextFieldName();
     assertEquals("a", fieldName);
     assertEquals("a", p.getValueAsString());
     assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
     p.close();
 }

 @Test
 public void testBinaryValueDecoding() throws Exception {
     JsonFactory f = new JsonFactory();
     JsonParser p = f.createParser("{\"data\":\"dGVzdA==\"}".getBytes("UTF-8"));
     assertToken(JsonToken.START_OBJECT, p.nextToken());
     assertToken(JsonToken.FIELD_NAME, p.nextToken());
     assertEquals("data", p.getValueAsString());
     assertToken(JsonToken.VALUE_STRING, p.nextToken());
     byte[] data = p.getBinaryValue(com.fasterxml.jackson.core.Base64Variants.getDefaultVariant());
     assertArrayEquals(new byte[] { 't', 'e', 's', 't' }, data);
     p.close();
 }

 @Test(expected = com.fasterxml.jackson.core.JsonParseException.class)
 public void testInvalidNumberThrowsException() throws Exception {
     JsonFactory f = new JsonFactory();
     JsonParser p = f.createParser("1.a".getBytes("UTF-8"));
     try {
         p.nextToken();
     } finally {
         p.close();
     }
 }
}
