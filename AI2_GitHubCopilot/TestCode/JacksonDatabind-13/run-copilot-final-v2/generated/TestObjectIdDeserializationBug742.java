package com.fasterxml.jackson.databind.struct;

 import static org.junit.Assert.*;

 import java.util.List;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.*;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;

 /**
  * Tests for [databind#742]: NullPointerException when ObjectId is null.
  */
 public class TestObjectIdDeserializationBug742
 {
     @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
     static class IdBean {
         public String id;
         public String name;

         @JsonIdentityReference(alwaysAsId = false)
         public IdBean next;

         public IdBean() { }
         public IdBean(String id, String name) {
             this.id = id;
             this.name = name;
         }
     }

     @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
     static class ForwardBean {
         public String id;
         public String payload;

         @JsonIdentityReference(alwaysAsId = false)
         public ForwardBean ref;

         public ForwardBean() { }
         public ForwardBean(String id, String payload) {
             this.id = id;
             this.payload = payload;
         }
     }

     private final ObjectMapper MAPPER = new ObjectMapper();

     // [databind#742] basic: deserialize a single object with null id
     @Test
     public void testNullObjectId() throws Exception {
         final String json = "{\"id\":null,\"name\":\"first\"}";
         IdBean bean = MAPPER.readValue(json, IdBean.class);
         assertNotNull(bean);
         assertNull(bean.id);
         assertEquals("first", bean.name);
     }

     // Multiple objects in array, all with null ids
     @Test
     public void testMultipleNullObjectIds() throws Exception {
         final String json = "[{\"id\":null,\"name\":\"a\"},{\"id\":null,\"name\":\"b\"}]";
         IdBean[] beans = MAPPER.readValue(json, IdBean[].class);
         assertNotNull(beans);
         assertEquals(2, beans.length);
         assertNull(beans[0].id);
         assertNull(beans[1].id);
         assertEquals("a", beans[0].name);
         assertEquals("b", beans[1].name);
     }

     // Mix of null and non-null ids in a single payload
     @Test
     public void testMixedNullAndNonNullIds() throws Exception {
         final String json = "[{\"id\":\"1\",\"name\":\"one\"},{\"id\":null,\"name\":\"nullish\"},{\
"id\":\"2\",\"name\":\"two\"}]";
         IdBean[] beans = MAPPER.readValue(json, IdBean[].class);
         assertNotNull(beans);
         assertEquals(3, beans.length);
         assertEquals("1", beans[0].id);
         assertEquals("one", beans[0].name);
         assertNull(beans[1].id);
         assertEquals("nullish", beans[1].name);
         assertEquals("2", beans[2].id);
         assertEquals("two", beans[2].name);
     }

     // Null id followed by a non-null forward reference should not trigger NPE
     @Test
     public void testForwardReferenceAfterNullId() throws Exception {
         final String json = "{\"id\":null,\"name\":\"nullObj\",\"next\":\"x\"}";
         // the reference "x" does not exist in the stream; should throw UnresolvedForwardReference,
not NPE
         try {
             MAPPER.readValue(json, IdBean.class);
             fail("Expected UnresolvedForwardReference");
         } catch (UnresolvedForwardReference e) {
             // expected, as long as it's not NPE
         }
     }

     // Forward reference with null id (both referring and target have null id)
     @Test
     public void testForwardReferenceBothNullId() throws Exception {
         final String json = "{\"id\":null,\"payload\":\"a\",\"ref\":null}";
         ForwardBean bean = MAPPER.readValue(json, ForwardBean.class);
         assertNotNull(bean);
         assertNull(bean.id);
         assertNull(bean.ref);
         assertEquals("a", bean.payload);
     }

     // Object with non-null id referring to another object that has null id
     @Test
     public void testNullIdAsReferenceTarget() throws Exception {
         // Two objects: first has non-null id, second has null id; reference from first to second
         final String json =
"[{\"id\":\"1\",\"name\":\"first\",\"next\":null},{\"id\":null,\"name\":\"nullTarget\"}]";
         IdBean[] beans = MAPPER.readValue(json, IdBean[].class);
         assertNotNull(beans);
         assertEquals(2, beans.length);
         assertEquals("1", beans[0].id);
         assertNotNull(beans[0].next); // null id object was resolved
         assertNull(beans[0].next.id);
         assertEquals("nullTarget", beans[0].next.name);
     }

     // Nested objects where inner object has null id
     @Test
     public void testNestedNullId() throws Exception {
         final String json =
"{\"id\":\"outer\",\"name\":\"outer\",\"next\":{\"id\":null,\"name\":\"inner\"}}";
         IdBean bean = MAPPER.readValue(json, IdBean.class);
         assertNotNull(bean);
         assertEquals("outer", bean.id);
         assertNotNull(bean.next);
         assertNull(bean.next.id);
         assertEquals("inner", bean.next.name);
     }

     // Edge: use ObjectReader with update value (calls set/setAndReturn path)
     @Test
     public void testUpdateWithNullId() throws Exception {
         IdBean existing = new IdBean("original", "old");
         final String json = "{\"id\":null,\"name\":\"updated\"}";
         IdBean result = MAPPER.readerForUpdating(existing).readValue(json);
         assertSame(existing, result);
         assertNull(result.id);
         assertEquals("updated", result.name);
     }

     // Multiple forward references with null ids interleaved
     @Test
     public void testChainedForwardRefsWithNullIds() throws Exception {
         // a -> nullA (null id), b -> nullB (null id), c -> "end" (non-null)
         final String json = "["
             + "{\"id\":null,\"payload\":\"nullA\",\"ref\":null},"
             + "{\"id\":null,\"payload\":\"nullB\",\"ref\":null},"
             + "{\"id\":\"end\",\"payload\":\"fin\"}"
             + "]";
         ForwardBean[] beans = MAPPER.readValue(json, ForwardBean[].class);
         assertNotNull(beans);
         assertEquals(3, beans.length);
         assertNull(beans[0].id);
         assertNull(beans[0].ref);
         assertNull(beans[1].id);
         assertNull(beans[1].ref);
         assertEquals("end", beans[2].id);
     }

     // Ensure that null id does not cause any identity resolution anomaly with repeated null
     @Test
     public void testRepeatedNullIdsIdentity() throws Exception {
         final String json = "[{\"id\":null,\"name\":\"first\"},{\"id\":null,\"name\":\"second\"}]";
         IdBean[] beans = MAPPER.readValue(json, IdBean[].class);
         assertNotNull(beans);
         assertEquals(2, beans.length);
         // Both should be deserialized (no NPE), regardless of identity semantics for null.
         assertNull(beans[0].id);
         assertNull(beans[1].id);
     }
 }
