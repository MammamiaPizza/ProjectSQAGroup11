package com.fasterxml.jackson.databind.objectid;

 import static org.junit.Assert.*;

 import org.junit.Before;
 import org.junit.Test;

 import com.fasterxml.jackson.annotation.*;
 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.*;

 /**
  * Tests for ObjectId handling together with {@link JsonFormat.Shape#ARRAY} and {@link
JsonCreator}.
  * Focused on the bug described in <a
href="]8;id=md-aojgrn;https://github.com/FasterXML/jackson-databind/issues/1261https://github.com/FasterXML/jackson-databind/issues/1261]8;;]8;;">issue #1261</a>.]8;;
  */
 public class ObjectIdWithArrayCreatorTest {

     private ObjectMapper mapper;

     @Before
     public void setUp() {
         mapper = new ObjectMapper();
     }

     // ---- Value types --------------------------------------------------

     @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
     @JsonFormat(shape = JsonFormat.Shape.ARRAY)
     static class User {
         public int id;
         public String name;

         @JsonCreator
         public User(@JsonProperty("id") int id, @JsonProperty("name") String name) {
             this.id = id;
             this.name = name;
         }
     }

     static class Container {
         public User user1;
         public User user2;
     }

     @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
     static class PojoUser {
         public int id;
         public String name;

         public PojoUser() {}
     }

     static class PojoContainer {
         public PojoUser user1;
         public PojoUser user2;
     }

     // ---- Tests --------------------------------------------------------

     /**
      * Core test for the bug: ObjectId reference should be deserializable when the creator is
      * array-based ({@code @JsonFormat(shape = ARRAY)}).
      */
     @Test
     public void testObjectIdWithArrayCreator() throws Exception {
         User user = new User(1, "John");
         Container input = new Container();
         input.user1 = user;
         input.user2 = user;   // same instance → second occurrence becomes a reference

         String json = mapper.writeValueAsString(input);
         Container output = mapper.readValue(json, Container.class);

         assertNotNull(output.user1);
         assertNotNull(output.user2);
         assertEquals(1, output.user1.id);
         assertEquals("John", output.user1.name);
         assertEquals(1, output.user2.id);
         assertEquals("John", output.user2.name);
         assertSame(output.user1, output.user2);
     }

     /**
      * Normal array-based creator deserialization (full array) must still work.
      */
     @Test
     public void testArrayCreatorWithoutObjectId() throws Exception {
         User user = mapper.readValue("[2,\"Jane\"]", User.class);
         assertEquals(2, user.id);
         assertEquals("Jane", user.name);
     }

     /**
      * ObjectId with standard object-shaped serialization (no array format) must not be affected.
      */
     @Test
     public void testObjectIdWithObjectCreator() throws Exception {
         PojoUser user = new PojoUser();
         user.id = 10;
         user.name = "Alice";

         PojoContainer input = new PojoContainer();
         input.user1 = user;
         input.user2 = user;

         String json = mapper.writeValueAsString(input);
         PojoContainer output = mapper.readValue(json, PojoContainer.class);

         assertNotNull(output.user1);
         assertNotNull(output.user2);
         assertSame(output.user1, output.user2);
     }

     /**
      * An ObjectId reference that appears before the definition should be resolved once the
definition is seen.
      */
     @Test
     public void testObjectIdReferenceBeforeDefinition() throws Exception {
         // user1 is the reference, user2 is the full object
         String json = "{\"user1\":[3],\"user2\":[3,\"Eve\"]}";
         Container output = mapper.readValue(json, Container.class);

         assertEquals("Eve", output.user1.name);
         assertEquals("Eve", output.user2.name);
         assertEquals(3, output.user1.id);
         assertEquals(3, output.user2.id);
         assertSame(output.user1, output.user2);
     }

     /**
      * Multiple references to the same id must resolve to the identical instance.
      */
     @Test
     public void testDuplicateObjectIdReferences() throws Exception {
         User user = new User(7, "Bob");
         Container input = new Container();
         input.user1 = user;
         input.user2 = user;

         String json = mapper.writeValueAsString(input);
         Container output = mapper.readValue(json, Container.class);
         assertSame(output.user1, output.user2);
     }

     /**
      * An array representing an ObjectId reference must contain exactly one element (the id).
      * An empty array should fail.
      */
     @Test(expected = JsonProcessingException.class)
     public void testEmptyArrayForObjectIdReference() throws Exception {
         mapper.readValue("{\"user1\":[1,\"John\"],\"user2\":[]}", Container.class);
     }

     /**
      * An ObjectId reference that points to an unknown id must produce an error.
      */
     @Test(expected = JsonProcessingException.class)
     public void testObjectIdReferenceWithUnknownId() throws Exception {
         mapper.readValue("{\"user1\":[1,\"John\"],\"user2\":[99]}", Container.class);
     }

     /**
      * A null id inside an ObjectId reference array should be treated as invalid.
      */
     @Test(expected = JsonProcessingException.class)
     public void testObjectIdReferenceWithNullId() throws Exception {
         mapper.readValue("{\"user1\":[1,\"John\"],\"user2\":[null]}", Container.class);
     }

     /**
      * Passing an array with more elements than expected when no ObjectId is involved must lead to
an error.
      */
     @Test(expected = JsonProcessingException.class)
     public void testArrayWithTooManyElements() throws Exception {
         mapper.readValue("[1,\"John\",true]", User.class);
     }

     /**
      * Boundary: array with an empty string for the name must be valid.
      */
     @Test
     public void testArrayWithEmptyName() throws Exception {
         User user = mapper.readValue("[6,\"\"]", User.class);
         assertEquals(6, user.id);
         assertEquals("", user.name);
     }
 }