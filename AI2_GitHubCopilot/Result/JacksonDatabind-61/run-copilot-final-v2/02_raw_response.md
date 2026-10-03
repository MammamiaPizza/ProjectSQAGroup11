import static org.junit.Assert.*;
 import org.junit.*;

 import com.fasterxml.jackson.core.type.TypeReference;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.exc.*;
 import com.fasterxml.jackson.core.JsonProcessingException;

 import java.util.*;

 /**
  * Tests for Defects4J bug 61: ObjectMapper with default typing incorrectly rejects
  * wrapper types for primitive fields (Long -> long, Integer -> int, etc.).
  * The fix should allow such deserialization without JsonMappingException.
  */
 public class PrimitiveWrapperDefaultTypingTest {

     private ObjectMapper createMapper() {
         ObjectMapper mapper = new ObjectMapper();
         mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
         return mapper;
     }

     static class LongKeyHolder {
         public long key;
     }

     static class IntKeyHolder {
         public int key;
     }

     static class BooleanKeyHolder {
         public boolean key;
     }

     static class DoubleKeyHolder {
         public double key;
     }

     static class LongArrayHolder {
         public long[] key;
     }

     /**
      * Bug scenario: deserializing a Long wrapper into a long field fails with
      * "Class java.lang.Long not subtype of [simple type, class long]".
      * After the fix this should succeed.
      */
     @Test
     public void testLongWrapperIntoLongField() throws Exception {
         ObjectMapper mapper = createMapper();
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("key", 123L);
         String json = mapper.writerFor(new TypeReference<HashMap<String, Object>>() {})
                 .writeValueAsString(map);
         LongKeyHolder result = mapper.readValue(json, LongKeyHolder.class);
         assertEquals(123L, result.key);
     }

     @Test
     public void testIntegerWrapperIntoIntField() throws Exception {
         ObjectMapper mapper = createMapper();
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("key", 42);
         String json = mapper.writerFor(new TypeReference<HashMap<String, Object>>() {})
                 .writeValueAsString(map);
         IntKeyHolder result = mapper.readValue(json, IntKeyHolder.class);
         assertEquals(42, result.key);
     }

     @Test
     public void testBooleanWrapperIntoBooleanField() throws Exception {
         ObjectMapper mapper = createMapper();
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("key", true);
         String json = mapper.writerFor(new TypeReference<HashMap<String, Object>>() {})
                 .writeValueAsString(map);
         BooleanKeyHolder result = mapper.readValue(json, BooleanKeyHolder.class);
         assertTrue(result.key);
     }

     @Test
     public void testDoubleWrapperIntoDoubleField() throws Exception {
         ObjectMapper mapper = createMapper();
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("key", 3.14d);
         String json = mapper.writerFor(new TypeReference<HashMap<String, Object>>() {})
                 .writeValueAsString(map);
         DoubleKeyHolder result = mapper.readValue(json, DoubleKeyHolder.class);
         assertEquals(3.14d, result.key, 0.0);
     }

     @Test
     public void testLongArrayDeserialization() throws Exception {
         ObjectMapper mapper = createMapper();
         LongArrayHolder holder = new LongArrayHolder();
         holder.key = new long[] { 1L, 2L, 3L };
         String json = mapper.writeValueAsString(holder);
         LongArrayHolder result = mapper.readValue(json, LongArrayHolder.class);
         assertArrayEquals(new long[] { 1L, 2L, 3L }, result.key);
     }

     @Test(expected = JsonMappingException.class)
     public void testNullForPrimitiveField() throws Exception {
         ObjectMapper mapper = createMapper();
         String json = "{\"key\":null}";
         mapper.readValue(json, LongKeyHolder.class); // null can't be assigned to long
     }

     @Test(expected = JsonMappingException.class)
     public void testMismatchedTypeId() throws Exception {
         ObjectMapper mapper = createMapper();
         // manually insert "java.lang.String" as type hint for a long field
         String json = "{\"key\":[\"java.lang.String\",\"not a number\"]}";
         mapper.readValue(json, LongKeyHolder.class);
     }

     @Test(expected = JsonMappingException.class)
     public void testUnknownClassName() throws Exception {
         ObjectMapper mapper = createMapper();
         String json = "{\"key\":[\"com.nonexistent.Fake\",\"dummy\"]}";
         mapper.readValue(json, LongKeyHolder.class);
     }

     @Test
     public void testPrimitiveFieldWithoutExplicitTypeInfo() throws Exception {
         ObjectMapper mapper = new ObjectMapper(); // no default typing
         String json = "{\"key\":123}";
         LongKeyHolder result = mapper.readValue(json, LongKeyHolder.class);
         assertEquals(123L, result.key);
     }
 }