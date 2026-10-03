package com.google.javascript.rhino.jstype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PrototypeObjectTypeDefects4JTest {

  private JSTypeRegistry newRegistry() {
    return new JSTypeRegistry(null, true);
  }

  private PrototypeObjectType newAnonymousObject(JSTypeRegistry registry) {
    return new PrototypeObjectType(registry, null, null);
  }

  private ObjectType recordWithProperty(
      JSTypeRegistry registry, String propertyName, JSType propertyType) {
    RecordTypeBuilder builder = new RecordTypeBuilder(registry);
    builder.addProperty(propertyName, propertyType, null);
    return (ObjectType) builder.build();
  }

  @Test
  public void testRecordConstraintInfersMissingStringPropertyAsOptional() {
    JSTypeRegistry registry = newRegistry();
    PrototypeObjectType target = newAnonymousObject(registry);
    ObjectType constraint =
        recordWithProperty(
            registry, "prop", registry.getNativeType(JSTypeNative.STRING_TYPE));

    target.matchRecordTypeConstraint(constraint);

    assertTrue(target.hasOwnProperty("prop"));
    assertTrue(target.getOwnPropertyNames().contains("prop"));
    assertNotNull(target.getSlot("prop"));
    assertTrue(target.isPropertyTypeInferred("prop"));
    assertEquals("(string|undefined)", target.getPropertyType("prop").toString());
  }

  @Test
  public void testMatchConstraintInfersAllMissingRecordProperties() {
    JSTypeRegistry registry = newRegistry();
    PrototypeObjectType target = newAnonymousObject(registry);
    RecordTypeBuilder builder = new RecordTypeBuilder(registry);
    builder.addProperty("a", registry.getNativeType(JSTypeNative.BOOLEAN_TYPE), null);
    builder.addProperty("b", registry.getNativeType(JSTypeNative.STRING_TYPE), null);
    ObjectType constraint = (ObjectType) builder.build();

    target.matchConstraint(constraint);

    assertEquals(2, target.getOwnPropertyNames().size());
    assertTrue(target.hasOwnProperty("a"));
    assertTrue(target.hasOwnProperty("b"));
    assertEquals("(boolean|undefined)", target.getPropertyType("a").toString());
    assertEquals("(string|undefined)", target.getPropertyType("b").toString());
    assertTrue(target.isPropertyTypeInferred("a"));
    assertTrue(target.isPropertyTypeInferred("b"));
  }

  @Test
  public void testRecordConstraintDoesNotReplaceDeclaredProperty() {
    JSTypeRegistry registry = newRegistry();
    PrototypeObjectType target = newAnonymousObject(registry);
    JSType booleanType = registry.getNativeType(JSTypeNative.BOOLEAN_TYPE);
    target.defineProperty("fixed", booleanType, false, null);
    ObjectType constraint =
        recordWithProperty(
            registry, "fixed", registry.getNativeType(JSTypeNative.STRING_TYPE));

    target.matchRecordTypeConstraint(constraint);

    assertTrue(target.hasOwnProperty("fixed"));
    assertTrue(target.isPropertyTypeDeclared("fixed"));
    assertFalse(target.isPropertyTypeInferred("fixed"));
    assertEquals("boolean", target.getPropertyType("fixed").toString());
  }

  @Test
  public void testNamedObjectDoesNotInferFromRecordConstraint() {
    JSTypeRegistry registry = newRegistry();
    PrototypeObjectType target = new PrototypeObjectType(registry, "NamedType", null);
    ObjectType constraint =
        recordWithProperty(
            registry, "prop", registry.getNativeType(JSTypeNative.STRING_TYPE));

    target.matchConstraint(constraint);

    assertFalse(target.hasOwnProperty("prop"));
    assertFalse(target.getOwnPropertyNames().contains("prop"));
  }
}
