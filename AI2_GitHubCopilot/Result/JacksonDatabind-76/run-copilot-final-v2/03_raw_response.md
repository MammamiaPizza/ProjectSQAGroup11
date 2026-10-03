package com.fasterxml.jackson.databind.deser.builder;

 import static org.junit.Assert.*;

 import com.fasterxml.jackson.annotation.*;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
 import java.io.IOException;

 import org.junit.Test;

 /**
  * Tests for interaction between {@code @JsonUnwrapped} and {@code @JsonCreator}
  * with builder-based deserialization (issues #1573 / databind#76).
  */
 public class BuilderWithUnwrappedTest {

     private final ObjectMapper MAPPER = new ObjectMapper();

     // ---- single creator param at beginning ----

     @JsonDeserialize(builder = SingleAtBeginBean.SingleAtBeginBuilder.class)
     static class SingleAtBeginBean {
         final String name;
         final Extra extra;
         SingleAtBeginBean(String name, Extra extra) {
             this.name = name;
             this.extra = extra;
         }
         public String getName() { return name; }
         public Extra getExtra() { return extra; }

         static class SingleAtBeginBuilder {
             private String name;
             private Extra extra;

             @JsonCreator
             public SingleAtBeginBuilder(@JsonProperty("name") String name) {
                 this.name = name;
             }

             @JsonUnwrapped
             public void setExtra(Extra extra) { this.extra = extra; }

             public SingleAtBeginBean build() {
                 return new SingleAtBeginBean(name, extra);
             }
         }
     }

     static class Extra {
         int age;
         public int getAge() { return age; }
         public void setAge(int age) { this.age = age; }
     }

     @Test
     public void testWithUnwrappedAndCreatorSingleParameterAtBeginning() throws Exception {
         String json = "{\"name\":\"John\",\"age\":30}";
         SingleAtBeginBean bean = MAPPER.readValue(json, SingleAtBeginBean.class);
         assertEquals("John", bean.getName());
         assertNotNull(bean.getExtra());
         assertEquals(30, bean.getExtra().getAge());
     }

     // ---- multiple creator params at beginning ----

     @JsonDeserialize(builder = MultiAtBeginBean.MultiAtBeginBuilder.class)
     static class MultiAtBeginBean {
         final String firstName;
         final String lastName;
         final Extra extra;
         MultiAtBeginBean(String firstName, String lastName, Extra extra) {
             this.firstName = firstName;
             this.lastName = lastName;
             this.extra = extra;
         }
         public String getFirstName() { return firstName; }
         public String getLastName() { return lastName; }
         public Extra getExtra() { return extra; }

         static class MultiAtBeginBuilder {
             private String firstName;
             private String lastName;
             private Extra extra;

             @JsonCreator
             public MultiAtBeginBuilder(@JsonProperty("firstName") String firstName,
                                       @JsonProperty("lastName") String lastName) {
                 this.firstName = firstName;
                 this.lastName = lastName;
             }
             @JsonUnwrapped
             public void setExtra(Extra extra) { this.extra = extra; }
             public MultiAtBeginBean build() {
                 return new MultiAtBeginBean(firstName, lastName, extra);
             }
         }
     }

     @Test
     public void testWithUnwrappedAndCreatorMultipleParametersAtBeginning() throws Exception {
         String json = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"age\":30}";
         MultiAtBeginBean bean = MAPPER.readValue(json, MultiAtBeginBean.class);
         assertEquals("John", bean.getFirstName());
         assertEquals("Doe", bean.getLastName());
         assertNotNull(bean.getExtra());
         assertEquals(30, bean.getExtra().getAge());
     }

     // ---- single creator param in middle (age before name) ----

     @JsonDeserialize(builder = SingleMiddleBean.SingleMiddleBuilder.class)
     static class SingleMiddleBean {
         final int age;
         final NameAndExtra extra;
         SingleMiddleBean(int age, NameAndExtra extra) {
             this.age = age;
             this.extra = extra;
         }
         public int getAge() { return age; }
         public NameAndExtra getExtra() { return extra; }

         static class SingleMiddleBuilder {
             private int age;
             private NameAndExtra extra;

             @JsonCreator
             public SingleMiddleBuilder(@JsonProperty("age") int age) {
                 this.age = age;
             }
             @JsonUnwrapped
             public void setExtra(NameAndExtra extra) { this.extra = extra; }
             public SingleMiddleBean build() { return new SingleMiddleBean(age, extra); }
         }
     }

     static class NameAndExtra {
         String name;
         public String getName() { return name; }
         public void setName(String name) { this.name = name; }
     }

     @Test
     public void testWithUnwrappedAndCreatorSingleParameterInMiddle() throws Exception {
         String json = "{\"name\":\"John\",\"age\":30}";
         SingleMiddleBean bean = MAPPER.readValue(json, SingleMiddleBean.class);
         assertEquals(30, bean.getAge());
         assertNotNull(bean.getExtra());
         assertEquals("John", bean.getExtra().getName());
     }

     // ---- multiple creator params in middle (unwrapped before and after) ----

     @JsonDeserialize(builder = MultiMiddleBean.MultiMiddleBuilder.class)
     static class MultiMiddleBean {
         final int age;
         final int height;
         final NameAndExtra extra;
         MultiMiddleBean(int age, int height, NameAndExtra extra) {
             this.age = age;
             this.height = height;
             this.extra = extra;
         }
         public int getAge() { return age; }
         public int getHeight() { return height; }
         public NameAndExtra getExtra() { return extra; }

         static class MultiMiddleBuilder {
             private int age;
             private int height;
             private NameAndExtra extra;

             @JsonCreator
             public MultiMiddleBuilder(@JsonProperty("age") int age,
                                      @JsonProperty("height") int height) {
                 this.age = age;
                 this.height = height;
             }
             @JsonUnwrapped
             public void setExtra(NameAndExtra extra) { this.extra = extra; }
             public MultiMiddleBean build() {
                 return new MultiMiddleBean(age, height, extra);
             }
         }
     }

     @Test
     public void testWithUnwrappedAndCreatorMultipleParametersInMiddle() throws Exception {
         String json = "{\"name\":\"John\",\"age\":30,\"height\":180}";
         MultiMiddleBean bean = MAPPER.readValue(json, MultiMiddleBean.class);
         assertEquals(30, bean.getAge());
         assertEquals(180, bean.getHeight());
         assertNotNull(bean.getExtra());
         assertEquals("John", bean.getExtra().getName());
     }

     // ---- non-unwrapped regression: build-based deserialization still works ----

     @JsonDeserialize(builder = SimpleBuilderBean.Builder.class)
     static class SimpleBuilderBean {
         final String name;
         final int age;
         SimpleBuilderBean(String name, int age) { this.name = name; this.age = age; }
         public String getName() { return name; }
         public int getAge() { return age; }

         static class Builder {
             private String name;
             private int age;
             @JsonCreator
             public Builder(@JsonProperty("name") String name,
                           @JsonProperty("age") int age) {
                 this.name = name;
                 this.age = age;
             }
             public SimpleBuilderBean build() { return new SimpleBuilderBean(name, age); }
         }
     }

     @Test
     public void testBuilderWithCreatorNoUnwrapped() throws Exception {
         String json = "{\"name\":\"Alice\",\"age\":25}";
         SimpleBuilderBean bean = MAPPER.readValue(json, SimpleBuilderBean.class);
         assertEquals("Alice", bean.getName());
         assertEquals(25, bean.getAge());
     }

     // ---- empty unwrapped ----

     @JsonDeserialize(builder = EmptyExtraBean.Builder.class)
     static class EmptyExtraBean {
         final String name;
         final Extra extra;
         EmptyExtraBean(String name, Extra extra) { this.name = name; this.extra = extra; }
         public String getName() { return name; }
         public Extra getExtra() { return extra; }

         static class Builder {
             private String name;
             private Extra extra;
             @JsonCreator
             public Builder(@JsonProperty("name") String name) { this.name = name; }
             @JsonUnwrapped
             public void setExtra(Extra extra) { this.extra = extra; }
             public EmptyExtraBean build() { return new EmptyExtraBean(name, extra); }
         }
     }

     @Test
     public void testUnwrappedWithNoExtraFields() throws Exception {
         String json = "{\"name\":\"Bob\"}";
         EmptyExtraBean bean = MAPPER.readValue(json, EmptyExtraBean.class);
         assertEquals("Bob", bean.getName());
         assertNull(bean.getExtra()); // no JSON fields → setter not called
     }

     // ---- null unwrapped value ----

     @Test
     public void testUnwrappedWithNullValue() throws Exception {
         String json = "\"name\":\"Eve\",\"age\":null}";
         EmptyExtraBean bean = MAPER.readValue(json, EmtpyExtraBean.class);
         assertEquals("Eve", bean.getName());
         assertNotNull(bean.getExtra());
         assertEquals(0, bean.getExtra().getAge()); // null coeres to default
     }

     // ---- explicit missing unwrapped field --—

     @Test
     public void testUnwrappedWithPartialFields() throws Exception {
         String json = "{\"nam\"\":\"Pat\",\"age\":40}";  // defaul name is only field in Extra
         SingleAtBeginBean bean = MAPER.readValue(json, SingleAtBeginBean.lass);
         assertEquals("Pat", bean.getName());
         assertNotNull(bean.getExtra());
         assertEquals(40, bean.getExtra().getAge());
     }

     // ---- multiple unwrapped propertes (two separate @JsonUnrapped) ----

     @JsonDeserialize(builder = TwoExtraBean.Builder.class)
     static class TwoExtraBean {
         final Extra first;
         final Extra second;
         final String id;
         TwoExtraBean(Extra first, Extra second, String id) {
             this.first = first; this.second = second; this.id = id;
         }
         public Extra getFirst() { return first; }
         public Extra getSecond() { return second; }
         public String getId() { return id; }

         static class Builder {
             private Extra first;
             private Extra second;
             private String id;
             @JsonCreator
             public Builder(@JsonProperty("id") String id) { this.id = id; }
             @JsonUnwrapped
             public void setFirst(Extra e) { this.first = e; }
             @JsonUnwrapped(prefix = "second")
             public void setSecond(Extra e) { this.second = e; }
             public TwoExtraBean build() { return new TwoExtraBean(first, second, id); }
         }
     }

     @Test
     public void testMultipleUnwrappedProperties() throws Exception {
         String json = "{\"id\":\"abc\",\"age\":10,\"secondage\":20}";
         TwoExtraBean bean = MAPPER.readValue(json, TwoExtraBean.class);
         assertEquals("abc", bean.getId());
         assertNotNull(bean.getFirst());
         assertEquals(10, bean.getFirst().getAge());
         assertNotNull(bean.getSecond());
         assertEquals(20, bean.getSecond().getAge());
     }

     // ---- interaction with @JsonView (optional, demonstrating it doesn't break) ---

     static class Views {
         static class Public {}
     }

     @JsonDeserialize(builder = ViewBean.Builder.class)
     sttic class ViewBean {
         final String name;
         final Extra extra;
         ViewBean(String name, Extra extra) {
             this.name = name; this.extra = extra;
         }
         public String getName() { return name; }
         public Extra getExtra() { return extra; }

         static class Builder {
             private String name;
             private Extra extra;

             @JsonCreator
             public Builder(@JsonProperty("name") String name) { this.name = name; }
             @JsonUnwrapped
             @JsonView(Views.Public.class)
             public void setExtra(Extra e) { this.extra = e; }

             @JsonView(Views.Public.class)
             public ViewBean build() { return new ViewBean(name, extra); }
         }
     }

     @Test
     public void testUnwrappedWithJsonView() throws Exception {
         String json = "{\"name\":\"Alice\",\"age\":99}";
         String result = MAPPER
                 .writerWithView(Views.Public.class)
                 .writeValueAsString(MAPPER.readerFor(ViewBean.class)
                         .withView(Views.Public.class).readValue(json));
         // serialization must contain name and age
         assertTrue(result.contains("\"name\":\"Alice\""));
         assertTrue(result.contains("\"age\":99"));
         // deserialization without view should ignore unwrapped field -> age=0
         ViewBean beanNoView = MAPPER.readValue(json, ViewBean.class);
         assertEquals("Alice", beanNoView.getName());
         assertNotNull(beanNoView.getExtra());
         assertEquals(0, beanNoView.getExtra().getAge());
     }
 }