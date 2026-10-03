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