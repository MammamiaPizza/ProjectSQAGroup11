package com.google.javascript.rhino.jstype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import org.junit.Test;

public class SemanticReverseAbstractInterpreterRegressionTest {

  private JSTypeRegistry newRegistry() {
    return new JSTypeRegistry(null);
  }

  @Test
  public void testCheckedUnknownTypeIsPreservedForTruthyBooleanOutcome() {
    JSTypeRegistry registry = newRegistry();
    JSType checkedUnknown =
        registry.getNativeType(JSTypeNative.CHECKED_UNKNOWN_TYPE);

    assertEquals("??", checkedUnknown.toString());
    assertSame(
        checkedUnknown,
        checkedUnknown.getRestrictedTypeGivenToBooleanOutcome(true));
  }

  @Test
  public void testCheckedUnknownTypeIsPreservedForFalsyBooleanOutcome() {
    JSTypeRegistry registry = newRegistry();
    JSType checkedUnknown =
        registry.getNativeType(JSTypeNative.CHECKED_UNKNOWN_TYPE);

    assertEquals("??", checkedUnknown.toString());
    assertSame(
        checkedUnknown,
        checkedUnknown.getRestrictedTypeGivenToBooleanOutcome(false));
  }

  @Test
  public void testNullCanOnlySatisfyFalsyBooleanOutcome() {
    JSTypeRegistry registry = newRegistry();
    JSType nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
    JSType noType = registry.getNativeType(JSTypeNative.NO_TYPE);

    assertSame(nullType, nullType.getRestrictedTypeGivenToBooleanOutcome(false));
    assertSame(noType, nullType.getRestrictedTypeGivenToBooleanOutcome(true));
  }

  @Test
  public void testVoidCanOnlySatisfyFalsyBooleanOutcome() {
    JSTypeRegistry registry = newRegistry();
    JSType voidType = registry.getNativeType(JSTypeNative.VOID_TYPE);
    JSType noType = registry.getNativeType(JSTypeNative.NO_TYPE);

    assertSame(voidType, voidType.getRestrictedTypeGivenToBooleanOutcome(false));
    assertSame(noType, voidType.getRestrictedTypeGivenToBooleanOutcome(true));
  }

  @Test
  public void testNoTypeRemainsImpossibleForEitherBooleanOutcome() {
    JSTypeRegistry registry = newRegistry();
    JSType noType = registry.getNativeType(JSTypeNative.NO_TYPE);

    assertSame(noType, noType.getRestrictedTypeGivenToBooleanOutcome(true));
    assertSame(noType, noType.getRestrictedTypeGivenToBooleanOutcome(false));
  }

  @Test
  public void testMissingPropertyInsideInCheckStillProducesWarningForObject() {
    com.google.javascript.jscomp.Compiler compiler =
        new com.google.javascript.jscomp.Compiler();
    CompilerOptions options = new CompilerOptions();
    options.setCheckTypes(true);

    compiler.compile(
        SourceFile.fromCode("externs.js", ""),
        SourceFile.fromCode(
            "input.js",
            "/** @param {Object} x */\n"
                + "function f(x) {\n"
                + "  if (!('missing' in x)) {\n"
                + "    x.missing;\n"
                + "  }\n"
                + "}\n"),
        options);

    assertTrue(
        "An in-check must not hide a missing-property type warning",
        compiler.getWarnings().length > 0);
  }

  @Test
  public void testMissingPropertyInsideInCheckStillProducesWarningForRecord() {
    com.google.javascript.jscomp.Compiler compiler =
        new com.google.javascript.jscomp.Compiler();
    CompilerOptions options = new CompilerOptions();
    options.setCheckTypes(true);

    compiler.compile(
        SourceFile.fromCode("externs.js", ""),
        SourceFile.fromCode(
            "input.js",
            "/** @param {{known: number}} x */\n"
                + "function f(x) {\n"
                + "  if (!('missing' in x)) {\n"
                + "    x.missing;\n"
                + "  }\n"
                + "}\n"),
        options);

    assertTrue(
        "An in-check must not manufacture an undeclared record property",
        compiler.getWarnings().length > 0);
  }
}
