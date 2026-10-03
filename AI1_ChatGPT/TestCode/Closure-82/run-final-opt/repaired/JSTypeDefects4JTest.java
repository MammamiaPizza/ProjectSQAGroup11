package com.google.javascript.rhino.jstype;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.ErrorReporter;
import org.junit.Test;

public class JSTypeDefects4JTest {

  private static final ErrorReporter SILENT_REPORTER =
      new ErrorReporter() {
        @Override
        public void warning(String message, String sourceName, int line, int lineOffset) {}

        @Override
        public void error(String message, String sourceName, int line, int lineOffset) {}

        @Override
        public RuntimeException runtimeError(
            String message, String sourceName, int line, String lineSource, int lineOffset) {
          return null;
        }
      };

  private JSTypeRegistry newRegistry() {
    return new JSTypeRegistry(SILENT_REPORTER);
  }

  @Test
  public void emptyFunctionTypesWithSameSignatureAreEquivalentAndMutualSubtypes() {
    JSTypeRegistry registry = newRegistry();
    JSType unknown = registry.getNativeType(JSTypeNative.UNKNOWN_TYPE);

    FunctionType first = registry.createFunctionType(unknown);
    FunctionType second = registry.createFunctionType(unknown);

    assertTrue(first.isEquivalentTo(second));
    assertTrue(second.isEquivalentTo(first));
    assertTrue(first.equals(second));
    assertTrue(first.isSubtype(second));
    assertTrue(second.isSubtype(first));
  }

  @Test
  public void emptyFunctionTypesWithDifferentReturnTypesAreNotEquivalent() {
    JSTypeRegistry registry = newRegistry();

    FunctionType returnsNumber =
        registry.createFunctionType(registry.getNativeType(JSTypeNative.NUMBER_TYPE));
    FunctionType returnsString =
        registry.createFunctionType(registry.getNativeType(JSTypeNative.STRING_TYPE));

    assertFalse(returnsNumber.isEquivalentTo(returnsString));
    assertFalse(returnsString.isEquivalentTo(returnsNumber));
    assertFalse(returnsNumber.isSubtype(returnsString));
    assertFalse(returnsString.isSubtype(returnsNumber));
  }

  @Test
  public void shallowEqualityOfEquivalentEmptyFunctionsKeepsTheirFunctionType() {
    JSTypeRegistry registry = newRegistry();
    JSType unknown = registry.getNativeType(JSTypeNative.UNKNOWN_TYPE);

    FunctionType first = registry.createFunctionType(unknown);
    FunctionType second = registry.createFunctionType(unknown);

    JSType.TypePair pair = first.getTypesUnderShallowEquality(second);

    assertNotNull(pair.typeA);
    assertNotNull(pair.typeB);
    assertTrue(pair.typeA.isEquivalentTo(first));
    assertTrue(pair.typeB.isEquivalentTo(first));
  }

  @Test
  public void equalityAndInequalityNarrowingForSamePrimitiveTypeRetainsTheType() {
    JSTypeRegistry registry = newRegistry();
    JSType number = registry.getNativeType(JSTypeNative.NUMBER_TYPE);

    JSType.TypePair equality = number.getTypesUnderEquality(number);
    JSType.TypePair inequality = number.getTypesUnderInequality(number);

    assertSame(number, equality.typeA);
    assertSame(number, equality.typeB);
    assertSame(number, inequality.typeA);
    assertSame(number, inequality.typeB);
  }

  @Test
  public void shallowInequalityOfTwoNullOrTwoVoidTypesIsImpossible() {
    JSTypeRegistry registry = newRegistry();
    JSType nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
    JSType voidType = registry.getNativeType(JSTypeNative.VOID_TYPE);

    JSType.TypePair nullPair = nullType.getTypesUnderShallowInequality(nullType);
    JSType.TypePair voidPair = voidType.getTypesUnderShallowInequality(voidType);

    assertNull(nullPair.typeA);
    assertNull(nullPair.typeB);
    assertNull(voidPair.typeA);
    assertNull(voidPair.typeB);
  }

  @Test
  public void staticEquivalenceHandlesNullAndDistinctNativeTypes() {
    JSTypeRegistry registry = newRegistry();
    JSType number = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
    JSType string = registry.getNativeType(JSTypeNative.STRING_TYPE);

    assertTrue(JSType.isEquivalent(null, null));
    assertFalse(JSType.isEquivalent(null, number));
    assertFalse(JSType.isEquivalent(number, null));
    assertFalse(JSType.isEquivalent(number, string));
  }
}
