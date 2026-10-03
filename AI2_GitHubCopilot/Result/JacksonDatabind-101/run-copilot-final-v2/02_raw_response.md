import com.fasterxml.jackson.annotation.JsonCreator;
 import com.fasterxml.jackson.annotation.JsonProperty;
 import com.fasterxml.jackson.annotation.JsonUnwrapped;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
 import org.junit.Test;

 import java.util.HashMap;
 import java.util.Map;

 import static org.junit.Assert.*;

 /**
  * JUnit tests for BeanDeserializer._deserializeUsingPropertyBased
  * when @JsonUnwrapped fields follow the last creator property.
  *
  * Bug: After the last creator param, remaining JSON tokens are not parsed
  * into the unwrapped map, resulting in an empty map (size 0 vs expected 4).
  */
 public class UnwrappedAfterCreatorTest {

     // --- Test bean classes ---

     public static class BeanWithUnwrappedAfterCreator {
         private final String name;

         @JsonUnwrapped
         private Map<String, String> extras = new HashMap<>();

         @JsonCreator
         public BeanWithUnwrappedAfterCreator(@JsonProperty("name") String name) {
             this.name = name;
         }

         public String getName() { return name; }
         public Map<String, String> getExtras() { return extras; }
     }

     public static class BeanWithMultipleCreatorParams {
         private final String first;
         private final String last;

         @JsonUnwrapped
         private Map<String, String> dynamic = new HashMap<>();

         @JsonCreator
         public BeanWithMultipleCreatorParams(
                 @JsonProperty("first") String first,
                 @JsonProperty("last")  String last) {
             this.first = first;
             this.last  = last;
         }

         public String getFirst()   { return first; }
         public String getLast()    { return last; }
         public Map<String, String> getDynamic() { return dynamic; }
     }

     public static class BeanWithOnlyCreatorParam {
         @JsonCreator
         public BeanWithOnlyCreatorParam(@JsonProperty("id") int id) { }
     }

     public static class BeanWithoutUnwrapped {
         private final String key;

         @JsonCreator
         public BeanWithoutUnwrapped(@JsonProperty("key") String key) {
             this.key = key;
         }

         public String getKey() { return key; }
     }

     // --- Tests ---

     /**
      * Normal case: 4 unwrapped fields after the last creator param.
      * The unwrapped map must contain exactly those 4 entries.
      */
     @Test
     public void testUnwrappedFieldsAfterLastCreatorProp() throws Exception {
         String json = "{\"name\":\"test\","
                      + "\"x\":\"1\",\"y\":\"2\",\"z\":\"3\",\"w\":\"4\"}";
         ObjectMapper mapper = new ObjectMapper();
         BeanWithUnwrappedAfterCreator bean =
                 mapper.readValue(json, BeanWithUnwrappedAfterCreator.class);

         assertEquals("test", bean.getName());
         Map<String, String> extras = bean.getExtras();
         assertNotNull(extras);
         assertEquals(4, extras.size());
         assertEquals("1", extras.get("x"));
         assertEquals("2", extras.get("y"));
         assertEquals("3", extras.get("z"));
         assertEquals("4", extras.get("w"));
     }

     /**
      * Boundary: no extra fields beyond the creator param.
      * The unwrapped map must be empty.
      */
     @Test
     public void testNoUnwrappedFields() throws Exception {
         String json = "{\"name\":\"only\"}";
         ObjectMapper mapper = new ObjectMapper();
         BeanWithUnwrappedAfterCreator bean =
                 mapper.readValue(json, BeanWithUnwrappedAfterCreator.class);

         assertEquals("only", bean.getName());
         assertNotNull(bean.getExtras());
         assertTrue(bean.getExtras().isEmpty());
     }

     /**
      * Edge: mixed – creator params are not the last property in JSON.
      * Unwrapped fields that appear before the last creator param should
      * also be collected.
      */
     @Test
     public void testUnwrappedFieldsInterleavedWithCreatorParams() throws Exception {
         String json = "{\"x\":\"1\",\"name\":\"mid\",\"y\":\"2\",\"z\":\"3\"}";
         ObjectMapper mapper = new ObjectMapper();
         BeanWithUnwrappedAfterCreator bean =
                 mapper.readValue(json, BeanWithUnwrappedAfterCreator.class);

         assertEquals("mid", bean.getName());
         Map<String, String> extras = bean.getExtras();
         assertEquals(3, extras.size());
         assertEquals("1", extras.get("x"));
         assertEquals("2", extras.get("y"));
         assertEquals("3", extras.get("z"));
     }

     /**
      * Edge: multiple creator params with unwrapped fields after all of them.
      */
     @Test
     public void testMultipleCreatorParamsWithUnwrapped() throws Exception {
         String json = "{\"first\":\"John\",\"last\":\"Doe\","
                      + "\"a\":\"apple\",\"b\":\"banana\",\"c\":\"cherry\"}";
         ObjectMapper mapper = new ObjectMapper();
         BeanWithMultipleCreatorParams bean =
                 mapper.readValue(json, BeanWithMultipleCreatorParams.class);

         assertEquals("John", bean.getFirst());
         assertEquals("Doe", bean.getLast());
         assertEquals(3, bean.getDynamic().size());
         assertEquals("apple",  bean.getDynamic().get("a"));
         assertEquals("banana", bean.getDynamic().get("b"));
         assertEquals("cherry", bean.getDynamic().get("c"));
     }

     /**
      * Error: missing required creator param when no unwrapped fields present.
      * Expect a {@code MismatchedInputException} (or subclass).
      */
     @Test(expected = com.fasterxml.jackson.databind.exc.MismatchedInputException.class)
     public void testMissingCreatorParam() throws Exception {
         String json = "{}"; // missing "id"
         ObjectMapper mapper = new ObjectMapper();
         mapper.readValue(json, BeanWithOnlyCreatorParam.class);
     }

     /**
      * Edge: only creator params, no unwrapped property declared.
      * Normal deserialization should still succeed.
      */
     @Test
     public void testOnlyCreatorParamWithoutUnwrapped() throws Exception {
         String json = "{\"key\":\"value\"}";
         ObjectMapper mapper = new ObjectMapper();
         BeanWithoutUnwrapped bean =
                 mapper.readValue(json, BeanWithoutUnwrapped.class);

         assertEquals("value", bean.getKey());
     }

     /**
      * Edge: extra fields when no @JsonUnwrapped is present.
      * By default, unknown properties should be ignored (or fail depending on config).
      * With default ObjectMapper, unknown properties are ignored.
      */
     @Test
     public void testExtraFieldsWithoutUnwrappedProperty() throws Exception {
         String json = "{\"key\":\"val\",\"unknown\":\"extra\"}";
         ObjectMapper mapper = new ObjectMapper();
         BeanWithoutUnwrapped bean =
                 mapper.readValue(json, BeanWithoutUnwrapped.class);
         // key must be present; unknown is silently ignored
         assertEquals("val", bean.getKey());
     }
 }