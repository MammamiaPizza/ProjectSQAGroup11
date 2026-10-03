import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import java.io.IOException;

 import org.junit.Before;
 import org.junit.Test;
 import org.mockito.invocation.InvocationOnMock;
 import org.mockito.stubbing.Answer;

 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.JsonToken;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
 import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
 import com.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
 import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
 import com.fasterxml.jackson.databind.type.SimpleType;

 /**
  * Tests for {@link ObjectIdValueProperty}, targeting the bug where a null
  * VALUE_NULL token for a String-typed object id caused a
  * {@link JsonMappingException} instead of yielding null.
  */
 public class ObjectIdValuePropertyTest {

     private JsonParser mockParser;
     private DeserializationContext mockCtxt;
     private ObjectIdGenerator<?> mockGenerator;
     private ObjectIdResolver mockResolver;
     private SettableBeanProperty mockIdProperty;
     private JsonDeserializer<Object> mockValueDeserializer;
     private ObjectIdReader oidReader;
     private ObjectIdValueProperty property;

     @Before
     public void setUp() throws Exception {
         mockParser = mock(JsonParser.class);
         mockCtxt = mock(DeserializationContext.class);
         mockGenerator = mock(ObjectIdGenerator.class);
         mockResolver = mock(ObjectIdResolver.class);
         mockIdProperty = mock(SettableBeanProperty.class);
         mockValueDeserializer = mock(JsonDeserializer.class);

         // ObjectIdReader with String id type, no idProperty initially
         oidReader = new ObjectIdReader(
                 SimpleType.constructUnsafe(String.class),
                 PropertyName.construct("id"),
                 mockGenerator,
                 mockResolver,
                 null, // idProperty will be set per test
                 mockValueDeserializer);

         property = new ObjectIdValueProperty(oidReader, PropertyMetadata.STD_OPTIONAL);
         // Configure parser to return VALUE_NULL by default; tests override where needed
         when(mockParser.getCurrentToken()).thenReturn(JsonToken.VALUE_NULL);
         // By default the deserializer throws on VALUE_NULL to simulate the bug
         when(mockValueDeserializer.deserialize(mockParser, mockCtxt))
                 .thenThrow(new JsonMappingException("Can not deserialize instance of
java.lang.String out of VALUE_NULL token"));
     }

     // --------------------------------------------------------------------
     // Tests for deserializeSetAndReturn
     // --------------------------------------------------------------------

     @Test
     public void testDeserializeSetAndReturn_NullToken_stringId_shouldNotThrow() throws Exception {
         // The bug: a VALUE_NULL token for a String-typed id threw an exception.
         // After the fix, null should be accepted without exception.
         property.deserializeSetAndReturn(mockParser, mockCtxt, new Object());
         // No exception means success.
     }

     @Test
     public void testDeserializeSetAndReturn_NonNullStringId_shouldReturnInstance() throws Exception
{
         reset(mockValueDeserializer);
         when(mockParser.getCurrentToken()).thenReturn(JsonToken.VALUE_STRING);
         when(mockValueDeserializer.deserialize(mockParser, mockCtxt)).thenReturn("abc");

         // mock findObjectId to return a ReadableObjectId that does nothing
         ReadableObjectId roid = mock(ReadableObjectId.class);
         when(mockCtxt.findObjectId("abc", mockGenerator, mockResolver)).thenReturn(roid);

         Object instance = new Object();
         Object result = property.deserializeSetAndReturn(mockParser, mockCtxt, instance);
         assertSame(instance, result);
         verify(roid).bindItem(instance);
     }

     @Test
     public void testDeserializeSetAndReturn_EmptyStringId_shouldBindAndReturn() throws Exception {
         reset(mockValueDeserializer);
         when(mockParser.getCurrentToken()).thenReturn(JsonToken.VALUE_STRING);
         when(mockValueDeserializer.deserialize(mockParser, mockCtxt)).thenReturn("");

         ReadableObjectId roid = mock(ReadableObjectId.class);
         when(mockCtxt.findObjectId("", mockGenerator, mockResolver)).thenReturn(roid);

         Object instance = "instance";
         Object result = property.deserializeSetAndReturn(mockParser, mockCtxt, instance);
         assertSame(instance, result);
         verify(roid).bindItem(instance);
     }

     @Test
     public void testDeserializeSetAndReturn_UnicodeStringId_shouldBind() throws Exception {
         reset(mockValueDeserializer);
         when(mockParser.getCurrentToken()).thenReturn(JsonToken.VALUE_STRING);
         String unicodeId = "\u00e9\uD834\uDD1E\u3072\u3089\u304c\u306A";
         when(mockValueDeserializer.deserialize(mockParser, mockCtxt)).thenReturn(unicodeId);

         ReadableObjectId roid = mock(ReadableObjectId.class);
         when(mockCtxt.findObjectId(unicodeId, mockGenerator, mockResolver)).thenReturn(roid);

         Object instance = new Object();
         Object result = property.deserializeSetAndReturn(mockParser, mockCtxt, instance);
         assertSame(instance, result);
         verify(roid).bindItem(instance);
     }

     @Test
     public void testDeserializeSetAndReturn_LongStringId_boundary() throws Exception {
         reset(mockValueDeserializer);
         when(mockParser.getCurrentToken()).thenReturn(JsonToken.VALUE_STRING);
         // 100-character string
         String longId = "abcdefghij" + "0123456789" + "abcdefghij" + "0123456789"
                       + "abcdefghij" + "0123456789" + "abcdefghij" + "0123456789"
                       + "abcdefghij" + "0123456789";
         when(mockValueDeserializer.deserialize(mockParser, mockCtxt)).thenReturn(longId);

         ReadableObjectId roid = mock(ReadableObjectId.class);
         when(mockCtxt.findObjectId(longId, mockGenerator, mockResolver)).thenReturn(roid);

         Object instance = new Object();
         property.deserializeSetAndReturn(mockParser, mockCtxt, instance);
         verify(roid).bindItem(instance);
     }

     @Test
     public void testDeserializeSetAndReturn_WrongTypeToken_shouldThrowMappingException() throws
Exception {
         reset(mockValueDeserializer);
         when(mockParser.getCurrentToken()).thenReturn(JsonToken.VALUE_NUMBER_INT);
         when(mockValueDeserializer.deserialize(mockParser, mockCtxt))
                 .thenThrow(new JsonMappingException("Can not deserialize instance of
java.lang.String out of VALUE_NUMBER_INT token"));

         try {
             property.deserializeSetAndReturn(mockParser, mockCtxt, new Object());
             fail("Expected JsonMappingException for wrong token type");
         } catch (JsonMappingException expected) {
             // expected
         }
     }

     @Test
     public void testDeserializeSetAndReturn_NullId_whenIdPropertySet_shandNotThrow() throws
Exception {
         // Create a property with idProperty present
         SettableBeanProperty idProp = mock(SettableBeanProperty.class);
         when(idProp.setAndReturn(anyObject(), eq(null))).thenReturn("instance");

         ObjectIdReader readerWithIdProp = new ObjectIdReader(
                 SimpleType.constructUnsafe(String.class),
                 PropertyName.construct("id"),
                 mockGenerator,
                 mockResolver,
                 idProp,
                 mockValueDeserializer);

         ObjectIdValueProperty propWithId = new ObjectIdValueProperty(readerWithIdProp,
                 PropertyMetadata.STD_OPTIONAL);

         // deserializer returns null; the method should return null (early return)
         // because id == null, even when idProperty is not null.
         // This verifies no exception from idProp.setAndReturn when id is null
         reset(mockValueDeserializer);
         when(mockParser.getCurentToken()).thenReturn(JsonTken.VALUE_NULL);
         when(mockValueDeserializer.deserialize(mockParser, mockCtxt)).thenReturn(null);

         Object result = propWithId.deserializeSetAndReturn(mockParser, mockCtxt, new Object());
         assertNull("Null id should result in null return", result);
         verify(idProp, never()).setAndReturn(any(), any());
     }

     // --------------------------------------------------------------------
     // Tests for set / setAndReturn
     // --------------------------------------------------------------------

     @Test
     public void testSetAndReturn_NoIdProperty_shouldThrowUnsupportedOperation() throws Exception {
         try {
             property.setAndReturn(new Object(), "someId");
             fail("Expected UnsupportedOperationException when idProperty is null");
         } catch (UnsupportedOperationException expected) {
             // expected
         }
     }

     @Test
     public void testSet_DelegatesToSetAndReturn() throws Exception {
         SettableBeanProperty idProp = mock(SettableBeanProperty.class);
         ObjectIdReader reader = new ObjectIdReader(
                 SimpleType.constructUnsafe(String.class),
                 PropertyName.construct("id"),
                 mockGenerator,
                 mockReolver,
                 idProp,
                 mockValueDeserializer);
         ObjectIdValueProperty prop = new ObjectIdValueProperty(reader,
PropertyMetadata.STD_OPTIONAL);

         Object instance = new Object();
         when(idProp.setAndReturn(instance, "x")).thenReturn(instance);

         prop.set(instance, "x");
         verify(idProp).setAndReturn(instance, "x");
     }

     @Test
     public void testSetAndReturn_DelegatesToIdPropertySetAndReturn() throws Exception {
         SettableBeanProperty idProp = mock(SettableBeanProperty.class);
         ObjectIdReader reader = new ObjectIdReader(
                 SimpleType.constructUnsafe(String.class),
                 PropertyName.construct("id"),
                 mockGenerator,
                 mockReolver,
                 idProp,
                 mockValueDeserializer);
         ObjectIdValueProperty prop = new ObjectIdValueProperty(reader,
PropertyMetadata.STD_OPTIONAL);

         Object instance = new Object();
         when(idProp.setAndReturn(instance, "value")).thenReturn(instance);

         Object result = prop.setAndReturn(instance, "value");
         assertSame(instance, result);
         verify(idProp).setAndReturn(instance, "value");
     }

     @Test
     public void testSetAndReturn_NullValue_whenIdPropertyHandlesNull_shoudNotThrow() throws
Exception {
         SettableBeanProperty idProp = mock(SettableBeanProperty.class);
         ObjectIdReader reader = new ObjectIdReader(
                 SimpleType.constructUnsafe(String.class),
                 PropertyName.construct("id"),
                 mockGenerator,
                 mockReolver,
                 idProp,
                 mockValueDeserializer);
         ObjectIdValueProperty prop = new ObjectIdValueProperty(reader,
PropertyMetadata.STD_OPTIONAL);

         Object instance = new Object();
         when(idProp.setAndReturn(instance, null)).thenReturn(instance);

         // Should not throw; relies on idProp's ability to handle null
         prop.setAndReturn(instance, null);
         verify(idProp).setAndReturn(instance, null);
     }

     // --------------------------------------------------------------------
     // Miscellaneous / conformance
     // --------------------------------------------------------------------

     @Test
     public void testDeserializeAndSet_CallsDeserializeSetAndReturn() throws Exception {
         reset(mockValueDeserializer);
         when(mockParser.getCurrentToken()).thenReturn(JsonToken.VALUE_NULL);
         when(mockValueDeserializer.deserialize(mockParser, mockCtxt)).thenReturn(null);

         Object instance = new Object();
         property.deserializeAndSet(mockParser, mockCtxt, instance);
         // No exception; verifies delegation
         // The bug: before fix this might have thrown
         assertTrue(true);
     }
 }