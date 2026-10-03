package com.google.javascript.rhino.jstype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class RecordTypeLeastSupertypeRegressionTest {
  private JSTypeRegistry registry;
  private JSType numberType;
  private JSType stringType;

  @Before
  public void setUp() {
    registry = new JSTypeRegistry(null);
    numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
    stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
  }

  @Test
  public void leastSupertypeOfOverlappingRecordsIsAUnion() {
    JSType left = record(
        new String[] {"a", "b"},
        new JSType[] {numberType, stringType});
    JSType right = record(
        new String[] {"b", "c", "e"},
        new JSType[] {stringType, stringType, numberType});

    assertEquals(
        "({a: number, b: string}|{b: string, c: string, e: number})",
        left.getLeastSupertype(right).toString());
  }

  @Test
  public void leastSupertypeOfDisjointRecordsIsAUnion() {
    JSType left = record(
        new String[] {"a", "b"},
        new JSType[] {numberType, stringType});
    JSType right = record(
        new String[] {"d", "e", "f"},
        new JSType[] {numberType, stringType, stringType});

    assertEquals(
        "({a: number, b: string}|{d: number, e: string, f: string})",
        left.getLeastSupertype(right).toString());
  }

  @Test
  public void leastSupertypePreservesAlternativesWithSharedProperties() {
    JSType left = record(
        new String[] {"a", "b"},
        new JSType[] {numberType, numberType});
    JSType right = record(
        new String[] {"b", "c"},
        new JSType[] {numberType, numberType});

    assertEquals(
        "({a: number, b: number}|{b: number, c: number})",
        left.getLeastSupertype(right).toString());
  }

  @Test
  public void leastSupertypeOfConflictingDeclaredPropertyTypesIsAUnion() {
    JSType numeric = record(
        new String[] {"a"},
        new JSType[] {numberType});
    JSType textual = record(
        new String[] {"a"},
        new JSType[] {stringType});

    assertEquals(
        "({a: number}|{a: string})",
        numeric.getLeastSupertype(textual).toString());
  }

  @Test
  public void recordWithAdditionalMatchingPropertiesIsSubtype() {
    JSType required = record(
        new String[] {"a"},
        new JSType[] {numberType});
    JSType withAdditionalProperty = record(
        new String[] {"a", "b"},
        new JSType[] {numberType, stringType});

    assertTrue(withAdditionalProperty.isSubtype(required));
    assertFalse(required.isSubtype(withAdditionalProperty));
  }

  @Test
  public void greatestSubtypeOfCompatibleRecordsContainsAllProperties() {
    JSType left = record(
        new String[] {"a", "b"},
        new JSType[] {numberType, numberType});
    JSType right = record(
        new String[] {"b", "c"},
        new JSType[] {numberType, numberType});
    JSType expected = record(
        new String[] {"a", "b", "c"},
        new JSType[] {numberType, numberType, numberType});

    assertTrue(expected.isEquivalentTo(left.getGreatestSubtype(right)));
  }

  private JSType record(String[] names, JSType[] types) {
    RecordTypeBuilder builder = new RecordTypeBuilder(registry);
    for (int i = 0; i < names.length; i++) {
      builder.addProperty(names[i], types[i], null);
    }
    return builder.build();
  }
}