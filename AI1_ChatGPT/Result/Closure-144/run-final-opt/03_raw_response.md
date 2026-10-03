package com.google.javascript.rhino.jstype;

import junit.framework.TestCase;
import org.junit.Test;

public class FunctionBuilderReturnTypeTest extends TestCase {

  private JSTypeRegistry newRegistry() {
    return new JSTypeRegistry(null);
  }

  @Test
  public void testBuildWithoutReturnTypeDefaultsToVoid() {
    JSTypeRegistry registry = newRegistry();

    FunctionType function = new FunctionBuilder(registry).build();

    assertSame(registry.getNativeType(JSTypeNative.UNKNOWN_TYPE), function.getReturnType());
  }

  @Test
  public void testConstructorWithoutReturnTypeDefaultsToVoid() {
    JSTypeRegistry registry = newRegistry();

    FunctionType constructor = new FunctionBuilder(registry).forConstructor().build();

    assertTrue(constructor.isConstructor());
    assertSame(registry.getNativeType(JSTypeNative.UNKNOWN_TYPE), constructor.getReturnType());
  }

  @Test
  public void testExplicitReturnTypeIsNotMarkedInferred() {
    JSTypeRegistry registry = newRegistry();
    JSType stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);

    FunctionType function =
        new FunctionBuilder(registry).withReturnType(stringType).build();

    assertSame(stringType, function.getReturnType());
    assertFalse(function.isReturnTypeInferred());
  }

  @Test
  public void testInferredReturnTypeAndItsInferenceFlagSurviveCopy() {
    JSTypeRegistry registry = newRegistry();
    JSType numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
    FunctionType original =
        new FunctionBuilder(registry).withInferredReturnType(numberType).build();

    FunctionType copy = new FunctionBuilder(registry).copyFromOtherFunction(original).build();

    assertSame(numberType, original.getReturnType());
    assertTrue(original.isReturnTypeInferred());
    assertSame(numberType, copy.getReturnType());
    assertFalse(copy.isReturnTypeInferred());
  }
}