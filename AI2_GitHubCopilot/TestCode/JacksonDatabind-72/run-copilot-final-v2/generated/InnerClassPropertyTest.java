package com.fasterxml.jackson.databind.deser.impl;

 import static org.junit.Assert.*;

 import java.lang.annotation.Annotation;
 import java.lang.reflect.Constructor;

 import org.junit.Before;
 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonCreator;
 import com.fasterxml.jackson.annotation.JsonProperty;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.databind.DeserializationContext;
 import com.fasterxml.jackson.databind.JsonDeserializer;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.PropertyName;
 import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
 import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
 import com.fasterxml.jackson.databind.introspect.AnnotatedMember;

 /**
  * Tests focused on the bug where InnerClassProperty fails to propagate
  * {@code assignIndex} to the delegate, causing an
  * {@code IllegalStateException: no creator index for property 'a'}
  * during inner-class deserialization (issue #1501).
  */
 public class InnerClassPropertyTest {

     private ObjectMapper mapper;

     @Before
     public void setUp() {
         mapper = new ObjectMapper();
     }

     // ---------- assignIndex / getPropertyIndex tests ----------

     /**
      * Verifies that the index assigned through the InnerClassProperty
      * is retrievable via getPropertyIndex. The bug caused the delegate
      * to remain unindexed, so this directly hits the faulty path.
      */
     @Test
     public void assignIndexAndRetrieveIt() throws Exception {
         Constructor<Outer.Inner> ctor = Outer.Inner.class.getDeclaredConstructor(Outer.class);
         // build a minimal delegate
         SettableBeanProperty delegate = createDelegate(new PropertyName("x"), ctor);
         InnerClassProperty prop = new InnerClassProperty(delegate, ctor);
         prop.assignIndex(3);
         assertEquals("assignIndex must propagate to delegate", 3, prop.getPropertyIndex());
     }

     /**
      * Assigning {@code 0} is the most common index; guarantees this
      * critical value works.
      */
     @Test
     public void assignIndexZero() throws Exception {
         Constructor<Outer.Inner> ctor = Outer.Inner.class.getDeclaredConstructor(Outer.class);
         SettableBeanProperty delegate = createDelegate(new PropertyName("x"), ctor);
         InnerClassProperty prop = new InnerClassProperty(delegate, ctor);
         prop.assignIndex(0);
         assertEquals(0, prop.getPropertyIndex());
     }

     /**
      * Multiple properties are each assigned distinct indices and the
      * values are retained independently.
      */
     @Test
     public void assignDistinctIndicesToMultipleProperties() throws Exception {
         Constructor<Outer.Inner> ctor = Outer.Inner.class.getDeclaredConstructor(Outer.class);
         SettableBeanProperty d1 = createDelegate(new PropertyName("a"), ctor);
         SettableBeanProperty d2 = createDelegate(new PropertyName("b"), ctor);
         InnerClassProperty p1 = new InnerClassProperty(d1, ctor);
         InnerClassProperty p2 = new InnerClassProperty(d2, ctor);
         p1.assignIndex(1);
         p2.assignIndex(2);
         assertEquals(1, p1.getPropertyIndex());
         assertEquals(2, p2.getPropertyIndex());
     }

     // ---------- Deserialization of a non-static inner class ----------

     /**
      * Core regression test for issue 1501: serializing and deserializing a
      * non-static inner class annotated with {@code @JsonCreator} must
      * succeed without {@code IllegalStateException: no creator index}.
      */
     @Test
     public void deserializeInnerClassWithAnnotatedConstructor() throws Exception {
         Outer outer = new Outer();
         outer.value = 99;
         String json = "{\"a\":42}";
         Outer.Inner inner = mapper.readValue(json, Outer.Inner.class);
         assertNotNull(inner);
         assertEquals(42, inner.a);
         assertSame(outer, inner.parent);
     }

     /**
      * Round-trip serialization then deserialization; the result must be
      * equivalent.
      */
     @Test
     public void roundTripInnerClass() throws Exception {
         Outer outer = new Outer();
         outer.value = 77;
         Outer.Inner original = new Outer.Inner(outer);
         original.a = 55;
         String json = mapper.writeValueAsString(original);
         Outer.Inner restored = mapper.readValue(json, Outer.Inner.class);
         assertEquals(original.a, restored.a);
         assertEquals(original.parent.value, restored.parent.value);
     }

     /**
      * Completely null inner-class property value should be handled.
      */
     @Test
     public void innerClassPropertyNullValue() throws Exception {
         String json = "{\"a\":null}";
         Outer.Inner inner = mapper.readValue(json, Outer.Inner.class);
         assertNotNull(inner);
         assertEquals(0, inner.a); // int defaults to 0 for null
     }

     /**
      * Missing property in JSON should result in default field value.
      */
     @Test
     public void innerClassPropertyMissing() throws Exception {
         String json = "{}";
         Outer.Inner inner = mapper.readValue(json, Outer.Inner.class);
         assertNotNull(inner);
         assertEquals(0, inner.a);
     }

     /**
      * Extra unknown properties should not break deserialization with
      * default config.
      */
     @Test
     public void innerClassPropertyUnknownFields() throws Exception {
         String json = "{\"a\":12,\"bogus\":999}";
         Outer.Inner inner = mapper.readValue(json, Outer.Inner.class);
         assertNotNull(inner);
         assertEquals(12, inner.a);
     }

     // ---------- withName uses delegate correctly ----------

     /**
      * {@code withName} must wrap the delegate so that {@code getFullName}
      * reflects the new name.
      */
     @Test
     public void withNameChangesPropertyName() throws Exception {
         Constructor<Outer.Inner> ctor = Outer.Inner.class.getDeclaredConstructor(Outer.class);
         SettableBeanProperty delegate = createDelegate(PropertyName.construct("old"), ctor);
         InnerClassProperty prop = new InnerClassProperty(delegate, ctor);
         InnerClassProperty renamed = prop.withName(new PropertyName("newName"));
         assertEquals("newName", renamed.getName());
     }

     /**
      * After renaming, getPropertyIndex must still return the assigned
      * index.
      */
     @Test
     public void withNamePreservesIndex() throws Exception {
         Constructor<Outer.Inner> ctor = Outer.Inner.class.getDeclaredConstructor(Outer.class);
         SettableBeanProperty delegate = createDelegate(PropertyName.construct("old"), ctor);
         InnerClassProperty prop = new InnerClassProperty(delegate, ctor);
         prop.assignIndex(7);
         InnerClassProperty renamed = prop.withName(new PropertyName("newName"));
         assertEquals(7, renamed.getPropertyIndex());
     }

     // ---------- annotated constructor can be constructed ----------

     /**
      * Verifies the annotated-constructor-based copy constructor does not
      * throw and retains delegate references.
      */
     @Test
     public void copyConstructorWithAnnotatedConstructor() throws Exception {
         Constructor<Outer.Inner> ctor = Outer.Inner.class.getDeclaredConstructor(Outer.class);
         SettableBeanProperty delegate = createDelegate(PropertyName.construct("p"), ctor);
         InnerClassProperty original = new InnerClassProperty(delegate, ctor);
         AnnotatedConstructor ann = new AnnotatedConstructor(null, ctor, null, null);
         InnerClassProperty copy = new InnerClassProperty(original, ann);
         assertNotNull(copy);
         assertEquals(original.getName(), copy.getName());
     }

     // ---------- helpers ----------

     private SettableBeanProperty createDelegate(PropertyName name,
             final Constructor<?> ctor) {
         return new SettableBeanProperty(name, null, null, null) {
             private int _index = -1;

             @Override
             public void assignIndex(int index) {
                 _index = index;
             }

             @Override
             public int getPropertyIndex() {
                 return _index;
             }

             @Override
             public SettableBeanProperty withName(PropertyName newName) {
                 return this; // simplified for test
             }

             @Override
             public SettableBeanProperty withValueDeserializer(
                     JsonDeserializer<?> deser) {
                 return this;
             }

             @Override
             public <A extends Annotation> A getAnnotation(Class<A> acls) {
                 return null;
             }

             @Override
             public AnnotatedMember getMember() {
                 return null;
             }

             @Override
             public void deserializeAndSet(JsonParser p,
                     DeserializationContext ctxt, Object bean)
                     throws java.io.IOException {
             }

             @Override
             public Object deserializeSetAndReturn(JsonParser p,
                     DeserializationContext ctxt, Object instance)
                     throws java.io.IOException {
                 return null;
             }

             @Override
             public void set(Object instance, Object value)
                     throws java.io.IOException {
             }

             @Override
             public Object setAndReturn(Object instance, Object value)
                     throws java.io.IOException {
                 return null;
             }
         };
     }

     // ---------- inner-class fixture for deserialization tests ----------

     public static class Outer {

         public int value;

         public class Inner {
             public int a;

             @JsonCreator
             public Inner(@JsonProperty("a") int a) {
                 this.a = a;
             }

             // Dummy parent-receiving constructor for assignIndex tests
             private Inner(Outer parent) {
                 Outer.this.value = parent.value;
             }
         }
     }
 }
