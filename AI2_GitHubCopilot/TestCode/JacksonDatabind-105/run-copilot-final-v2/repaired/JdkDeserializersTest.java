package com.fasterxml.jackson.databind.deser.std;

 import static org.junit.Assert.*;

 import java.nio.ByteBuffer;
 import java.util.UUID;
 import java.util.concurrent.atomic.AtomicBoolean;

 import org.junit.Test;

 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.exc.MismatchedInputException;

 /**
  * Tests for {@link JdkDeserializers} focusing on the Void deserialization bug
  * (issue 2197).
  */
 public class JdkDeserializersTest {

     private final ObjectMapper mapper = new ObjectMapper();

     // --- find() regression tests for supported types ---

     @Test
     public void testFindReturnsNonNullForUUID() {
         assertNotNull(JdkDeserializers.find(UUID.class, "java.util.UUID"));
     }

     @Test
     public void testFindReturnsNonNullForAtomicBoolean() {
         assertNotNull(JdkDeserializers.find(AtomicBoolean.class,
"java.util.concurrent.atomic.AtomicBoolean"));
     }

     @Test
     public void testFindReturnsNonNullForStackTraceElement() {
         assertNotNull(JdkDeserializers.find(StackTraceElement.class,
"java.lang.StackTraceElement"));
     }

     @Test
     public void testFindReturnsNonNullForByteBuffer() {
         assertNotNull(JdkDeserializers.find(ByteBuffer.class, "java.nio.ByteBuffer"));
     }

     @Test
     public void testFindReturnsNullForUnknownClass() {
         assertNull(JdkDeserializers.find(java.awt.Point.class, "java.awt.Point"));
     }

     // --- Void: find() should return a deserializer (bug if null) ---

     @Test
     public void testFindVoidClassReturnsNonNull() {
         assertNotNull("JdkDeserializers must handle java.lang.Void",
                 JdkDeserializers.find(Void.class, "java.lang.Void"));
     }

     // --- Void deserialization integration tests (expected: always null, never
MismatchedInputException) ---

     @Test
     public void testVoidDeserFromNull() throws Exception {
         try {
             assertNull(mapper.readValue("null", Void.class));
         } catch (MismatchedInputException e) {
             fail("Void deserialization from null must not throw MismatchedInputException");
         }
     }

     @Test
     public void testVoidDeserFromNumber() throws Exception {
         try {
             assertNull(mapper.readValue("123", Void.class));
         } catch (MismatchedInputException e) {
             fail("Void deserialization from number must not throw MismatchedInputException");
         }
     }

     @Test
     public void testVoidDeserFromString() throws Exception {
         try {
             assertNull(mapper.readValue("\"abc\"", Void.class));
         } catch (MismatchedInputException e) {
             fail("Void deserialization from string must not throw MismatchedInputException");
         }
     }

     @Test
     public void testVoidDeserFromEmptyObject() throws Exception {
         try {
             assertNull(mapper.readValue("{}", Void.class));
         } catch (MismatchedInputException e) {
             fail("Void deserialization from empty object must not throw MismatchedInputException");
         }
     }

     @Test
     public void testVoidDeserFromEmptyArray() throws Exception {
         try {
             assertNull(mapper.readValue("[]", Void.class));
         } catch (MismatchedInputException e) {
             fail("Void deserialization from empty array must not throw MismatchedInputException");
         }
     }
 }
