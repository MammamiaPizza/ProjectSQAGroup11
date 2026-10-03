package com.google.javascript.rhino.jstype;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RecordTypeIssue725Test {

  private JSTypeRegistry newRegistry() {
    return new JSTypeRegistry(null);
  }

  private JSType recordWithProperty(
      JSTypeRegistry registry, String propertyName, JSType propertyType) {
    return new RecordTypeBuilder(registry)
        .addProperty(propertyName, propertyType, null)
        .build();
  }

  @Test
  public void testEmptyRecordBuildsNativeObjectType() {
    JSTypeRegistry registry = newRegistry();

    JSType record = new RecordTypeBuilder(registry).build();

    assertSame(registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE), record);
  }

  @Test
  public void testObjectWithoutRequiredPropertyIsNotSubtypeOfRecord() {
    JSTypeRegistry registry = newRegistry();
    JSType requiredRecord =
        recordWithProperty(
            registry, "required", registry.getNativeType(JSTypeNative.NUMBER_TYPE));
    ObjectType objectWithoutProperty = registry.createAnonymousObjectType();

    assertFalse(
        RecordType.isSubtype(
            objectWithoutProperty, requiredRecord.toMaybeRecordType()));
  }

  @Test
  public void testObjectWithMatchingRequiredPropertyIsSubtypeOfRecord() {
    JSTypeRegistry registry = newRegistry();
    JSType requiredRecord =
        recordWithProperty(
            registry, "required", registry.getNativeType(JSTypeNative.NUMBER_TYPE));
    ObjectType objectWithProperty = registry.createAnonymousObjectType();

    assertTrue(
        objectWithProperty.defineDeclaredProperty(
            "required", registry.getNativeType(JSTypeNative.NUMBER_TYPE), null));
    assertTrue(
        RecordType.isSubtype(
            objectWithProperty, requiredRecord.toMaybeRecordType()));
  }

  @Test
  public void testObjectWithIncompatiblePropertyIsNotSubtypeOfRecord() {
    JSTypeRegistry registry = newRegistry();
    JSType requiredRecord =
        recordWithProperty(
            registry, "required", registry.getNativeType(JSTypeNative.NUMBER_TYPE));
    ObjectType objectWithWrongPropertyType = registry.createAnonymousObjectType();

    assertTrue(
        objectWithWrongPropertyType.defineDeclaredProperty(
            "required", registry.getNativeType(JSTypeNative.STRING_TYPE), null));
    assertFalse(
        RecordType.isSubtype(
            objectWithWrongPropertyType, requiredRecord.toMaybeRecordType()));
  }

  @Test
  public void testRecordWithAdditionalPropertiesIsSubtypeOfRequiredRecord() {
    JSTypeRegistry registry = newRegistry();
    RecordTypeBuilder widerBuilder = new RecordTypeBuilder(registry);
    widerBuilder.addProperty(
        "a", registry.getNativeType(JSTypeNative.NUMBER_TYPE), null);
    widerBuilder.addProperty(
        "b", registry.getNativeType(JSTypeNative.STRING_TYPE), null);
    JSType widerRecord = widerBuilder.build();
    JSType narrowerRecord =
        recordWithProperty(
            registry, "a", registry.getNativeType(JSTypeNative.NUMBER_TYPE));

    assertTrue(widerRecord.isSubtype(narrowerRecord));
  }

  @Test
  public void testRecordMissingRequiredPropertyIsNotSubtype() {
    JSTypeRegistry registry = newRegistry();
    JSType recordWithoutRequiredProperty =
        recordWithProperty(
            registry, "other", registry.getNativeType(JSTypeNative.NUMBER_TYPE));
    JSType requiredRecord =
        recordWithProperty(
            registry, "required", registry.getNativeType(JSTypeNative.NUMBER_TYPE));

    assertFalse(recordWithoutRequiredProperty.isSubtype(requiredRecord));
  }

  @Test
  public void testRecordEquivalenceRequiresSamePropertyNamesAndTypes() {
    JSTypeRegistry registry = newRegistry();
    JSType numberRecord =
        recordWithProperty(
            registry, "value", registry.getNativeType(JSTypeNative.NUMBER_TYPE));
    JSType sameNumberRecord =
        recordWithProperty(
            registry, "value", registry.getNativeType(JSTypeNative.NUMBER_TYPE));
    JSType stringRecord =
        recordWithProperty(
            registry, "value", registry.getNativeType(JSTypeNative.STRING_TYPE));
    JSType differentNameRecord =
        recordWithProperty(
            registry, "other", registry.getNativeType(JSTypeNative.NUMBER_TYPE));

    assertTrue(numberRecord.isEquivalentTo(sameNumberRecord));
    assertFalse(numberRecord.isEquivalentTo(stringRecord));
    assertFalse(numberRecord.isEquivalentTo(differentNameRecord));
  }

  @Test
  public void testDuplicateRecordPropertyIsRejectedAndDoesNotReplaceOriginalType() {
    JSTypeRegistry registry = newRegistry();
    RecordTypeBuilder builder = new RecordTypeBuilder(registry);

    assertTrue(
        builder.addProperty(
                "value", registry.getNativeType(JSTypeNative.NUMBER_TYPE), null)
            != null);
    assertNull(
        builder.addProperty(
            "value", registry.getNativeType(JSTypeNative.STRING_TYPE), null));

    JSType builtRecord = builder.build();
    JSType expectedNumberRecord =
        recordWithProperty(
            registry, "value", registry.getNativeType(JSTypeNative.NUMBER_TYPE));

    assertTrue(builtRecord.isEquivalentTo(expectedNumberRecord));
  }
}