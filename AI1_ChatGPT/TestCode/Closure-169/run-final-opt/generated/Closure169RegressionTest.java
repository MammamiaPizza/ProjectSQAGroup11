package com.google.javascript.rhino.jstype;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class Closure169RegressionTest {
  private JSTypeRegistry registry;
  private JSType numberType;
  private JSType stringType;
  private JSType unknownType;

  @Before
  public void setUp() {
    registry = new JSTypeRegistry(null);
    numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
    stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
    unknownType = registry.getNativeType(JSTypeNative.UNKNOWN_TYPE);
  }

  @Test
  public void testRecordMissingRequiredPropertyIsNotSubtypeWhenPresentPropertyIsUnknown() {
    JSType source = record("a", unknownType);
    JSType target = record("a", numberType, "b", numberType);

    assertFalse(source.isSubtype(target));
  }

  @Test
  public void testRecordMissingRequiredPropertyIsNotSubtypeWhenTargetPropertyIsUnknown() {
    JSType source = record("a", numberType);
    JSType target = record("a", numberType, "b", unknownType);

    assertFalse(source.isSubtype(target));
  }

  @Test
  public void testRecordUnknownPropertyCanSatisfyKnownRequiredProperty() {
    JSType source = record("a", unknownType);
    JSType target = record("a", numberType);

    assertTrue(source.isSubtype(target));
  }

  @Test
  public void testRecordWithAdditionalPropertiesIsSubtypeOfNarrowerRecord() {
    JSType source = record("a", numberType, "b", stringType);
    JSType target = record("a", numberType);

    assertTrue(source.isSubtype(target));
  }

  @Test
  public void testRecordWithIncompatiblePropertyTypeIsNotSubtype() {
    JSType source = record("a", stringType);
    JSType target = record("a", numberType);

    assertFalse(source.isSubtype(target));
  }

  @Test
  public void testUnionIsSubtypeOnlyWhenEveryAlternateIsSubtype() {
    JSType numberOrString = registry.createUnionType(numberType, stringType);

    assertFalse(numberOrString.isSubtype(numberType));
    assertTrue(numberType.isSubtype(numberOrString));
  }

  @Test
  public void testUnionContainingRecordMissingRequiredPropertyIsNotRecordSubtype() {
    JSType incomplete = record("a", unknownType);
    JSType complete = record("a", unknownType, "b", numberType);
    JSType union = registry.createUnionType(incomplete, complete);
    JSType required = record("a", unknownType, "b", numberType);

    assertFalse(union.isSubtype(required));
  }

  @Test
  public void testArrowReturnTypeIsCovariant() {
    ArrowType returnsNumber = new ArrowType(registry, null, numberType);
    ArrowType returnsUnknown = new ArrowType(registry, null, unknownType);
    ArrowType returnsString = new ArrowType(registry, null, stringType);

    assertTrue(returnsNumber.isSubtype(returnsUnknown));
    assertFalse(returnsNumber.isSubtype(returnsString));
  }

  private JSType record(String property, JSType type) {
    RecordTypeBuilder builder = new RecordTypeBuilder(registry);
    builder.addProperty(property, type, null);
    return builder.build();
  }

  private JSType record(
      String firstProperty, JSType firstType, String secondProperty, JSType secondType) {
    RecordTypeBuilder builder = new RecordTypeBuilder(registry);
    builder.addProperty(firstProperty, firstType, null);
    builder.addProperty(secondProperty, secondType, null);
    return builder.build();
  }
}
