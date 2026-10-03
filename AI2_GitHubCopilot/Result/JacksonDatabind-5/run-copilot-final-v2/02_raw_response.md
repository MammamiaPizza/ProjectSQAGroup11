package com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector.MixInResolver;

import org.junit.Test;
import static org.junit.Assert.*;

/**

 - Tests that verify mix-in annotations are correctly applied to target
 - classes via {@link AnnotatedClass} methods such as resolveMemberMethods,
 - resolveFields, _addMixUnders, and _addClassMixIns.
 - The bug (Jackson#515) causes mix-in annotations to disappear, leading to
 - "No serializer found" because annotations from mix-in methods/fields are
 - not merged into the target class's annotated members.
  */
 public class TestDisappearingMixins {
  // ---- test method annotations via mixin ----
  @Test
  public void testMixinMethodAnnotationApplied() {
  AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(
          TargetWithGetter.class,
          new JacksonAnnotationIntrospector(),
          resolverFor(TargetWithGetter.class, MixinForGetter.class));
  AnnotatedMethod method = ac.findMethod("getName", new Class<?>[0]);
  assertNotNull("getName should exist", method);
  JsonProperty prop = method.getAnnotation(JsonProperty.class);
  assertNotNull("JsonProperty should be applied from mixin", prop);
  assertEquals("getterName", prop.value());
  }
  // ---- test field annotations via mixin ----
  @Test
  public void testMixinFieldAnnotationApplied() {
  AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(
          TargetWithField.class,
          new JacksonAnnotationIntrospector(),
          resolverFor(TargetWithField.class, MixinForField.class));
  AnnotatedField field = findField(ac, "value");
  assertNotNull("value field should exist", field);
  JsonIgnore ignore = field.getAnnotation(JsonIgnore.class);
  assertNotNull("JsonIgnore should be applied from mixin", ignore);
  }
  // ---- test class-level annotation via mixin ----
  @Test
  public void testMixinClassAnnotationApplied() {
  AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(
          SimpleTarget.class,
          new JacksonAnnotationIntrospector(),
          resolverFor(SimpleTarget.class, MixinWithClassAnnotation.class));
  // MixinWithClassAnnotation is annotated with @Deprecated
  Deprecated dep = ac.getAnnotation(Deprecated.class);
  assertNotNull("Class-level annotation from mixin should be applied", dep);
  }
  // ---- test mixin inherits annotations from its own superclass ----
  @Test
  public void testMixinSuperclassMethodAnnotations() {
  // mixin subclass (MixinSub) does not redefine getAge; annotation from MixinSuper should apply
  AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(
          TargetWithAge.class,
          new JacksonAnnotationIntrospector(),
          resolverFor(TargetWithAge.class, MixinSub.class));
  AnnotatedMethod method = ac.findMethod("getAge", new Class<?>[0]);
  assertNotNull("getAge should exist", method);
  JsonProperty prop = method.getAnnotation(JsonProperty.class);
  assertNotNull("JsonProperty from mixin superclass should be inherited", prop);
  assertEquals("age", prop.value());
  }
  // ---- mixin resolver that returns null – still functional ----
  @Test
  public void testMixinResolverReturnsNull() {
  MixInResolver nullResolver = new MixInResolver() {
      @Override
      public Class<?> findMixInClassFor(Class<?> cls) {
          return null;
      }
  };
  AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(
          TargetWithGetter.class,
          new JacksonAnnotationIntrospector(),
          nullResolver);
  AnnotatedMethod method = ac.findMethod("getName", new Class<?>[0]);
  assertNotNull("getName should still exist", method);
  // no mixin applied, so no JsonProperty
  assertNull("No mixin annotation expected", method.getAnnotation(JsonProperty.class));
  }
  // ---- mixin overrides an annotation already present on the target ----
  @Test
  public void testMixinAnnotationOverridesExisting() {
  AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(
          TargetWithAnnotatedGetter.class,
          new JacksonAnnotationIntrospector(),
          resolverFor(TargetWithAnnotatedGetter.class, MixinWithOverride.class));
  AnnotatedMethod method = ac.findMethod("getName", new Class<?>[0]);
  assertNotNull("getName should exist", method);
  JsonProperty prop = method.getAnnotation(JsonProperty.class);
  assertNotNull("JsonProperty should be present", prop);
  // mixin overrides "oldName" -> "newName"
  assertEquals("newName", prop.value());
  }
  // ---- multiple methods receive mixin annotations ----
  @Test
  public void testMixinMultipleMethods() {
  AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(
          TargetMulti.class,
          new JacksonAnnotationIntrospector(),
          resolverFor(TargetMulti.class, MixinMulti.class));
  AnnotatedMethod m1 = ac.findMethod("getX", new Class<?>[0]);
  AnnotatedMethod m2 = ac.findMethod("getY", new Class<?>[0]);
  assertNotNull(m1);
  assertNotNull(m2);
  assertEquals("x", m1.getAnnotation(JsonProperty.class).value());
  assertEquals("y", m2.getAnnotation(JsonProperty.class).value());
  }
  // ---- no MixInResolver – original annotations survive ----
  @Test
  public void testNoMixinResolver() {
  // Target has its own @JsonProperty; should be visible without any mixin
  AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(
          TargetWithAnnotatedGetter.class,
          new JacksonAnnotationIntrospector(),
          null);
  AnnotatedMethod method = ac.findMethod("getName", new Class<?>[0]);
  assertNotNull(method);
  JsonProperty prop = method.getAnnotation(JsonProperty.class);
  assertNotNull("Own annotation should still be present without mixin resolver", prop);
  assertEquals("oldName", prop.value());
  }
  // ---- serialization with mixin registered via ObjectMapper ----
  @Test
  public void testSerializationWithMixin() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  mapper.addMixInAnnotations(TargetWithGetter.class, MixinForGetter.class);
  TargetWithGetter obj = new TargetWithGetter();
  obj.setName("John");
  String json = mapper.writeValueAsString(obj);
  assertTrue("JSON should contain mixin-specified property name", json.contains(""getterName""));
  assertFalse("JSON should not contain original property name", json.contains(""name""));
  }
  // ---- field ignored via mixin during serialization ----
  @Test
  public void testFieldIgnoredViaMixin() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  mapper.addMixInAnnotations(TargetWithField.class, MixinForField.class);
  TargetWithField obj = new TargetWithField();
  obj.value = 42;
  String json = mapper.writeValueAsString(obj);
  // value field should not appear because of @JsonIgnore from mixin
  assertFalse("Field should be ignored", json.contains(""value""));
  }
  // ---- helper inner classes ----
  static class TargetWithGetter {
  private String name;
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  }
  static abstract class MixinForGetter {
  @JsonProperty("getterName")
  public abstract String getName();
  }
  static class TargetWithField {
  public int value;
  }
  static abstract class MixinForField {
  @JsonIgnore
  public int value;
  }
  static class SimpleTarget { }
  @Deprecated
  static abstract class MixinWithClassAnnotation { }
  // superclass chain for mixin superclass test
  static class TargetWithAge {
  public int getAge() { return 0; }
  }
  static abstract class MixinSuper {
  @JsonProperty("age")
  public abstract int getAge();
  }
  static abstract class MixinSub extends MixinSuper { }
  // override scenario
  static class TargetWithAnnotatedGetter {
  @JsonProperty("oldName")
  private String name;
  public String getName() { return name; }
  }
  static abstract class MixinWithOverride {
  @JsonProperty("newName")
  public abstract String getName();
  }
  // multiple method mixins
  static class TargetMulti {
  public int getX() { return 1; }
  public int getY() { return 2; }
  }
  static abstract class MixinMulti {
  @JsonProperty("x")
  public abstract int getX();
  @JsonProperty("y")
  public abstract int getY();
  }
  // --- helper utilities ---
  private static MixInResolver resolverFor(final Class<?> target, final Class<?> mixin) {
  return new MixInResolver() {
      @Override
      public Class<?> findMixInClassFor(Class<?> cls) {
          return cls == target ? mixin : null;
      }
  };
  }
  private static AnnotatedField findField(AnnotatedClass ac, String name) {
  for (AnnotatedField f : ac.fields()) {
      if (f.getName().equals(name)) return f;
  }
  return null;
  }

}