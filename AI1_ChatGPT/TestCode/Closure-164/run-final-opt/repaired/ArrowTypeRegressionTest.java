package com.google.javascript.rhino.jstype;

import static com.google.javascript.rhino.jstype.JSTypeNative.BOOLEAN_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.NO_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.STRING_TYPE;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArrowTypeRegressionTest {

  private JSTypeRegistry newRegistry() {
    return new JSTypeRegistry(null);
  }

  @Test
  public void testFunctionWithMoreRequiredParametersIsNotSubtype() {
    JSTypeRegistry registry = newRegistry();
    JSType number = registry.getNativeType(NUMBER_TYPE);
    JSType bool = registry.getNativeType(BOOLEAN_TYPE);

    ArrowType oneParameter =
        new ArrowType(registry, registry.createParameters(number), bool);
    ArrowType twoParameters =
        new ArrowType(registry, registry.createParameters(number, number), bool);

    assertFalse(twoParameters.isSubtype(oneParameter));
  }

  @Test
  public void testFunctionWithFewerRequiredParametersIsSubtype() {
    JSTypeRegistry registry = newRegistry();
    JSType number = registry.getNativeType(NUMBER_TYPE);
    JSType bool = registry.getNativeType(BOOLEAN_TYPE);

    ArrowType oneParameter =
        new ArrowType(registry, registry.createParameters(number), bool);
    ArrowType twoParameters =
        new ArrowType(registry, registry.createParameters(number, number), bool);

    assertTrue(oneParameter.isSubtype(twoParameters));
  }

  @Test
  public void testLeastSupertypeIsSupertypeOfBothArrows() {
    JSTypeRegistry registry = newRegistry();
    JSType number = registry.getNativeType(NUMBER_TYPE);
    JSType bool = registry.getNativeType(BOOLEAN_TYPE);
    JSType noType = registry.getNativeType(NO_TYPE);

    ArrowType booleanResult =
        new ArrowType(registry, registry.createParameters(number, number), bool);
    ArrowType emptyResult =
        new ArrowType(registry, registry.createParameters(number, number), noType);

    JSType result = booleanResult.getLeastSupertype(emptyResult);

    assertTrue(booleanResult.isSubtype(result));
    assertTrue(emptyResult.isSubtype(result));
  }

  @Test
  public void testGreatestSubtypeIsSubtypeOfBothArrows() {
    JSTypeRegistry registry = newRegistry();
    JSType number = registry.getNativeType(NUMBER_TYPE);
    JSType bool = registry.getNativeType(BOOLEAN_TYPE);
    JSType noType = registry.getNativeType(NO_TYPE);

    ArrowType booleanResult =
        new ArrowType(registry, registry.createParameters(number, number), bool);
    ArrowType emptyResult =
        new ArrowType(registry, registry.createParameters(number, number), noType);

    JSType result = booleanResult.getGreatestSubtype(emptyResult);

    assertTrue(result.isSubtype(booleanResult));
    assertTrue(result.isSubtype(emptyResult));
  }

  @Test
  public void testFunctionLeastSupertypeRetainsTwoParameterFunctionType() {
    JSTypeRegistry registry = newRegistry();
    JSType number = registry.getNativeType(NUMBER_TYPE);
    JSType bool = registry.getNativeType(BOOLEAN_TYPE);
    JSType noType = registry.getNativeType(NO_TYPE);

    FunctionType booleanFunction =
        registry.createFunctionType(bool, registry.createParameters(number, number));
    FunctionType emptyFunction =
        registry.createFunctionType(noType, registry.createParameters(number, number));

    assertEquals(
        "function (number, number): boolean",
        booleanFunction.getLeastSupertype(emptyFunction).toString());
  }

  @Test
  public void testDifferentParameterCountsAreNotEquivalent() {
    JSTypeRegistry registry = newRegistry();
    JSType number = registry.getNativeType(NUMBER_TYPE);
    JSType bool = registry.getNativeType(BOOLEAN_TYPE);

    ArrowType oneParameter =
        new ArrowType(registry, registry.createParameters(number), bool);
    ArrowType twoParameters =
        new ArrowType(registry, registry.createParameters(number, number), bool);

    assertFalse(oneParameter.isEquivalentTo(twoParameters));
  }

  @Test
  public void testDifferentParameterTypesAreNotEquivalent() {
    JSTypeRegistry registry = newRegistry();
    JSType number = registry.getNativeType(NUMBER_TYPE);
    JSType string = registry.getNativeType(STRING_TYPE);
    JSType bool = registry.getNativeType(BOOLEAN_TYPE);

    ArrowType numberParameter =
        new ArrowType(registry, registry.createParameters(number, number), bool);
    ArrowType stringParameter =
        new ArrowType(registry, registry.createParameters(number, string), bool);

    assertFalse(numberParameter.hasEqualParameters(stringParameter));
  }
}
