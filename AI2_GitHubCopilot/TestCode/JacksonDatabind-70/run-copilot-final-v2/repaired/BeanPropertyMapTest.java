package com.fasterxml.jackson.databind.deser.impl;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import java.io.IOException;
 import java.util.*;
 import java.lang.annotation.Annotation;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.databind.DeserializationContext;
 import com.fasterxml.jackson.databind.JavaType;
 import com.fasterxml.jackson.databind.JsonMappingException;
 import com.fasterxml.jackson.databind.SerializerProvider;
 import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
 import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
 import com.fasterxml.jackson.databind.node.ObjectNode;
 import com.fasterxml.jackson.databind.util.NameTransformer;

 public class BeanPropertyMapTest {

     private static class TestProperty extends SettableBeanProperty {
         private int index = -1;
         private final String name;

         public TestProperty(String name) {
             super(name, null, null, null, null, false);
             this.name = name;
         }

         @Override public void assignIndex(int index) { this.index = index; }
         @Override public int getPropertyIndex() { return index; }
         @Override public String getName() { return name; }
         @Override public JavaType getType() { return null; }
         @Override public AnnotatedMember getMember() { return null; }
         @Override public <A extends Annotation> A getAnnotation(Class<A> cls) { return null; }
         @Override public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object
instance) throws IOException { }
         @Override public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt,
Object instance) throws IOException { return instance; }
         @Override public void set(Object instance, Object value) throws IOException { }
         @Override public Object setAndReturn(Object instance, Object value) throws IOException {
return instance; }
         @Override public Object getInjectableValueId() { return null; }
         @Override public void depositSchemaProperty(ObjectNode propertiesNode, SerializerProvider
provider) throws JsonMappingException { }
     }

     private BeanPropertyMap map(boolean caseInsensitive, String... names) {
         List<SettableBeanProperty> props = new ArrayList<>();
         for (String n : names) props.add(new TestProperty(n));
         return new BeanPropertyMap(caseInsensitive, props);
     }

     @Test
     public void testRemoveExisting_SameCase() {
         BeanPropertyMap m = map(false, "a", "b");
         SettableBeanProperty toRemove = m.find("a");
         assertNotNull(toRemove);
         m.remove(toRemove);
         assertEquals(1, m.size());
         assertNull(m.find("a"));
         assertNotNull(m.find("b"));
     }

     @Test
     public void testRemoveExisting_CaseInsensitive_DifferentCase() {
         BeanPropertyMap m = map(true, "businessAddress");
         SettableBeanProperty toRemove = m.find("businessAddress");
         assertNotNull(toRemove);
         m.remove(toRemove);
         assertEquals(0, m.size());
         assertNull(m.find("businessAddress"));
     }

     @Test
     public void testRemoveNonExisting_ThrowsException() {
         BeanPropertyMap m = map(false, "a");
         try {
             m.remove(new TestProperty("b"));
             fail("Expected NoSuchElementException");
         } catch (NoSuchElementException e) {
             // expected
         }
     }

     @Test
     public void testRemoveEmptyMapThrows() {
         BeanPropertyMap m = map(false);
         try {
             m.remove(new TestProperty("x"));
             fail("Expected NoSuchElementException");
         } catch (NoSuchElementException e) { }
     }

     @Test
     public void testGetPropertiesInInsertionOrderAfterRemove_HasNul() {
         BeanPropertyMap m = map(true, "foo", "bar", "qux");
         SettableBeanProperty p = m.find("bar");
         m.remove(p);
         SettableBeanProperty[] arr = m.getPropertiesInInsertionOrder();
         assertEquals(3, arr.length);
         assertNull(arr[1]);
         assertEquals("foo", arr[0].getName());
         assertEquals("qux", arr[2].getName());
     }

     @Test
     public void testReplaceExisting() {
         BeanPropertyMap m = map(false, "a");
         SettableBeanProperty old = m.find("a");
         SettableBeanProperty replacement = new TestProperty("a");
         m.replace(replacement);
         assertSame(replacement, m.find("a"));
     }

     @Test
     public void testReplaceNonExistingThrows() {
         BeanPropertyMap m = map(false, "a");
         try {
             m.replace(new TestProperty("b"));
             fail("Expected NoSuchElementException");
         } catch (NoSuchElementException e) { }
     }

     @Test
     public void testWithPropertyAddsNew() {
         BeanPropertyMap m = map(false, "a");
         SettableBeanProperty newProp = new TestProperty("b");
         BeanPropertyMap updated = m.withProperty(newProp);
         assertEquals(2, updated.size());
         assertNotNull(updated.find("b"));
     }

     @Test
     public void testWithoutPropertiesExcludes() {
         BeanPropertyMap m = map(false, "a", "b", "c");
         BeanPropertyMap filtered = m.withoutProperties(new HashSet<>(Arrays.asList("b")));
         assertEquals(2, filtered.size());
         assertNull(filtered.find("b"));
         assertNotNull(filtered.find("a"));
     }

     @Test
     public void testAsignIndexes() {
         BeanPropertyMap m = map(false, "x", "y");
         m.assignIndexes();
         SettableBeanProperty p0 = m.find(0);
         SettableBeanProperty p1 = m.find(1);
         assertNotNull(p0);
         assertNotNull(p1);
         assertNotSame(p0, p1);
     }

     @Test
     public void testRenameAl_RemoveWorks() {
         BeanPropertyMap m = map(true, "origName");
         NameTransformer transf = NameTransformer.simpleTransformer("p_", null);
         BeanPropertyMap renamed = m.renameAll(transf);
         SettableBeanProperty rp = renamed.find("p_origName");
         assertNotNull(rp);
         renamed.remove(rp);
         assertEquals(0, renamed.size());
     }

     @Test
     public void testCaseInsensitivityCtorFlag() {
         BeanPropertyMap sensitive = map(false, "a");
         BeanPropertyMap insensitive = sensitive.withCaseInsensitivity(true);
         assertTrue(insensitive._caseInsensitive);
         assertFalse(sensitive._caseInsensitive);
     }

 }
