package com.fasterxml.jackson.databind.deser.std;

 import static org.junit.Assert.*;

 import java.io.IOException;

 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.mockito.Mock;
 import org.mockito.Mockito;
 import org.mockito.MockitoAnnotations;

 import com.fasterxml.jackson.core.*;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

 /**
  * Tests for {@link NullifyingDeserializer}, targeting the bug where
  * {@code deserialize} fails to advance the parser beyond the current
  * value for non-container tokens, leading to
  * {@link UnrecognizedPropertyException} in polymorphic default
  * implementations.
  */
 @RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
 public class NullifyingDeserializerTest {

     private NullifyingDeserializer deserializer;

     @Mock
     private DeserializationContext ctxt;

     @Before
     public void setUp() {
         MockitoAnnotations.initMocks(this);
         deserializer = NullifyingDeserializer.instance;
     }

     // -- deserialize --------------------------------------------------------

     @Test
     public void testDeserializeEmptyObject() throws IOException {
         JsonParser p = parser("{}");
         p.nextToken(); // START_OBJECT
         Object result = deserializer.deserialize(p, ctxt);
         assertNull(result);
         // after skipChildren from START_OBJECT, parser must be past END_OBJECT
         assertNull(p.getCurrentToken());
     }

     @Test
     public void testDeserializeSimpleObject() throws IOException {
         JsonParser p = parser("{\"a\":1,\"b\":\"two\"}");
         p.nextToken(); // START_OBJECT
         Object result = deserializer.deserialize(p, ctxt);
         assertNull(result);
         assertNull(p.getCurrentToken());
     }

     @Test
     public void testDeserializeNestedObject() throws IOException {
         JsonParser p = parser("{\"x\":{\"y\":{\"z\":true}}}");
         p.nextToken(); // START_OBJECT
         Object result = deserializer.deserialize(p, ctxt);
         assertNull(result);
         assertNull(p.getCurrentToken());
     }

     @Test
     public void testDeserializeEmptyArray() throws IOException {
         JsonParser p = parser("[]");
         p.nextToken(); // START_ARRAY
         Object result = deserializer.deserialize(p, ctxt);
         assertNull(result);
         assertNull(p.getCurrentToken());
     }

     @Test
     public void testDeserializeMixedArray() throws IOException {
         JsonParser p = parser("[1, \"two\", true, null, {\"inner\":3}, [4]]");
         p.nextToken(); // START_ARRAY
         Object result = deserializer.deserialize(p, ctxt);
         assertNull(result);
         assertNull(p.getCurrentToken());
     }

     @Test
     public void testDeserializeAtFieldName() throws IOException {
         // parser positioned at a field name; skipChildren only skips the field's value
         JsonParser p = parser("{\"field\":\"value\"}");
         p.nextToken(); // START_OBJECT
         p.nextToken(); // FIELD_NAME "field"
         assertEquals(JsonToken.FIELD_NAME, p.getCurrentToken());
         Object result = deserializer.deserialize(p, ctxt);
         assertNull(result);
         // after skipping the string value, parser is on END_OBJECT
         assertEquals(JsonToken.END_OBJECT, p.getCurrentToken());
     }

     @Test
     public void testDeserializeAtFieldNameWithObjectValue() throws IOException {
         JsonParser p = parser("{\"field\":{\"inner\":true}}");
         p.nextToken(); // START_OBJECT
         p.nextToken(); // FIELD_NAME "field"
         Object result = deserializer.deserialize(p, ctxt);
         assertNull(result);
         // after skipping the object value, parser is on END_OBJECT of the outer object
         assertEquals(JsonToken.END_OBJECT, p.getCurrentToken());
     }

     @Test
     public void testDeserializeScalarString() throws IOException {
         JsonParser p = parser("\"hello\"");
         p.nextToken(); // VALUE_STRING
         Object result = deserializer.deserialize(p, ctxt);
         assertNull(result);
         // buggy: skipChildren does nothing for scalar tokens, so parser stays put
         assertEquals(JsonToken.VALUE_STRING, p.getCurrentToken());
     }

     @Test
     public void testDeserializeScalarNumber() throws IOException {
         JsonParser p = parser("42");
         p.nextToken(); // VALUE_NUMBER_INT
         Object result = deserializer.deserialize(p, ctxt);
         assertNull(result);
         assertEquals(JsonToken.VALUE_NUMBER_INT, p.getCurrentToken());
     }

     @Test
     public void testDeserializeScalarBoolean() throws IOException {
         JsonParser p = parser("true");
         p.nextToken(); // VALUE_TRUE
         Object result = deserializer.deserialize(p, ctxt);
         assertNull(result);
         assertEquals(JsonToken.VALUE_TRUE, p.getCurrentToken());
     }

     @Test
     public void testDeserializeScalarNull() throws IOException {
         JsonParser p = parser("null");
         p.nextToken(); // VALUE_NULL
         Object result = deserializer.deserialize(p, ctxt);
         assertNull(result);
         assertEquals(JsonToken.VALUE_NULL, p.getCurrentToken());
     }

     // -- deserializeWithType -------------------------------------------------

     @Test
     public void testDeserializeWithTypeObject() throws IOException {
         TypeDeserializer typeDeser = Mockito.mock(TypeDeserializer.class);
         JsonParser p = parser("{}");
         p.nextToken(); // START_OBJECT
         deserializer.deserializeWithType(p, ctxt, typeDeser);
         // must delegate to typeDeserializer.deserializeTypedFromAny
         Mockito.verify(typeDeser).deserializeTypedFromAny(p, ctxt);
     }

     // helper
     private JsonParser parser(String json) throws IOException {
         return new JsonFactory().createParser(json);
     }
 }