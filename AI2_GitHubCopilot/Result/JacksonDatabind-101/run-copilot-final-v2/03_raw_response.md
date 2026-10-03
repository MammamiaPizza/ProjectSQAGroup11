import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class UnwrappedAfterCreatorTest {

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
     public BeanWithOnlyCreatorParam(@JsonProperty("id") Integer id) { }
 }

 @JsonIgnoreProperties(ignoreUnknown = true)
 public static class BeanWithoutUnwrapped {
     private final String key;

     @JsonCreator
     public BeanWithoutUnwrapped(@JsonProperty("key") String key) {
         this.key = key;
     }

     public String getKey() { return key; }
 }

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

 @Test(expected = com.fasterxml.jackson.databind.exc.MismatchedInputException.class)
 public void testMissingCreatorParam() throws Exception {
     String json = "{}";
     ObjectMapper mapper = new ObjectMapper();
     mapper.readValue(json, BeanWithOnlyCreatorParam.class);
 }

 @Test
 public void testOnlyCreatorParamWithoutUnwrapped() throws Exception {
     String json = "{\"key\":\"value\"}";
     ObjectMapper mapper = new ObjectMapper();
     BeanWithoutUnwrapped bean =
             mapper.readValue(json, BeanWithoutUnwrapped.class);

     assertEquals("value", bean.getKey());
 }

 @Test
 public void testExtraFieldsWithoutUnwrappedProperty() throws Exception {
     String json = "{\"key\":\"val\",\"unknown\":\"extra\"}";
     ObjectMapper mapper = new ObjectMapper();
     BeanWithoutUnwrapped bean =
             mapper.readValue(json, BeanWithoutUnwrapped.class);
     assertEquals("val", bean.getKey());
 }

}