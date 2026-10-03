package com.google.javascript.rhino.jstype;

import junit.framework.TestCase;

public class JSTypeEqualityNarrowingTest extends TestCase {

  private JSTypeRegistry registry;
  private JSType voidType;
  private JSType nullType;
  private JSType numberType;
  private JSType noType;

  @Override
  protected void setUp() {
    registry = new JSTypeRegistry(null);
    voidType = registry.getNativeType(JSTypeNative.VOID_TYPE);
    nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
    numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
    noType = registry.getNativeType(JSTypeNative.NO_TYPE);
  }

  public void testEqualityOfUndefinedAndNumberRetainsBothTypes() {
    JSType.TypePair pair = voidType.getTypesUnderEquality(numberType);

    assertSame(voidType, pair.typeA);
    assertSame(numberType, pair.typeB);
  }

  public void testEqualityOfUndefinedAndNullRetainsBothTypes() {
    JSType.TypePair pair = voidType.getTypesUnderEquality(nullType);

    assertSame(voidType, pair.typeA);
    assertSame(nullType, pair.typeB);
  }

  public void testInequalityOfUndefinedAndNullProducesNoTypes() {
    JSType.TypePair pair = voidType.getTypesUnderInequality(nullType);

    assertSame(noType, pair.typeA);
    assertSame(noType, pair.typeB);
  }

  public void testInequalityOfUndefinedAndNumberRetainsBothTypes() {
    JSType.TypePair pair = voidType.getTypesUnderInequality(numberType);

    assertSame(voidType, pair.typeA);
    assertSame(numberType, pair.typeB);
  }
}
