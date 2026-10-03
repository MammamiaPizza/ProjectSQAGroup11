package com.google.javascript.jscomp;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;

 import com.google.javascript.jscomp.SourceFile;
 import com.google.javascript.rhino.jstype.FunctionBuilder;
 import com.google.javascript.rhino.jstype.FunctionParamBuilder;
 import com.google.javascript.rhino.jstype.FunctionType;
 import com.google.javascript.rhino.jstype.JSType;
 import com.google.javascript.rhino.jstype.JSTypeNative;
 import com.google.javascript.rhino.jstype.JSTypeRegistry;

 import org.junit.Before;
 import org.junit.Test;

 /**

 - Regression tests for Bug 144: function types without an explicit return annotation
 - should default to VOID_TYPE (printed as "undefined"), not UNKNOWN_TYPE ("?").
   */
  public class FunctionTypeRegressionTest {
  private JSTypeRegistry registry;
  private Compiler compiler;
  @Before
  public void setUp() {
  registry = new JSTypeRegistry(new com.google.javascript.rhino.ErrorReporter() {
      @Override
      public void warning(String message, String sourceName, int line, int lineOffset) { }
      @Override
      public void error(String message, String sourceName, int line, int lineOffset) { }
  }, false);
  compiler = new Compiler();
  CompilerOptions options = new CompilerOptions();
  options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.ERROR);
  options.setIdeMode(true); // allows partial compilation without full externs
  compiler.initOptions(options);
  }
  // ----------------------------------------------------------------------------
  //  Unit tests via FunctionBuilder
  // ----------------------------------------------------------------------------
  @Test
  public void testDefaultReturnTypeIsVoid_FunctionBuilder() {
  // A function with no return type set should default to void (undefined).
  FunctionBuilder builder = new FunctionBuilder(registry)
          .withParams(new FunctionParamBuilder(registry));
  FunctionType ft = builder.build();
  assertEquals("function (): undefined", ft.toString());
  assertEquals(registry.getNativeType(JSTypeNative.VOID_TYPE), ft.getReturnType());
  assertTrue(ft.getReturnType().isVoidType());
  }
  @Test
  public void testExplicitReturnType_FunctionBuilder() {
  // An explicitly set return type must be preserved.
  FunctionBuilder builder = new FunctionBuilder(registry)
          .withParams(new FunctionParamBuilder(registry))
          .withReturnType(registry.getNativeType(JSTypeNative.NUMBER_TYPE));
  FunctionType ft = builder.build();
  assertEquals("function (): number", ft.toString());
  assertEquals(registry.getNativeType(JSTypeNative.NUMBER_TYPE), ft.getReturnType());
  }
  @Test
  public void testUnknownReturnTypeAnnotation_FunctionBuilder() {
  // When user writes @return {?}, the type must stay unknown ("?") and not become void.
  FunctionBuilder builder = new FunctionBuilder(registry)
          .withParams(new FunctionParamBuilder(registry))
          .withReturnType(registry.getNativeType(JSTypeNative.UNKNOWN_TYPE));
  FunctionType ft = builder.build();
  assertEquals("function (): ?", ft.toString());
  assertEquals(registry.getNativeType(JSTypeNative.UNKNOWN_TYPE), ft.getReturnType());
  }
  @Test
  public void testInferredReturnTypeVoid_FunctionBuilder() {
  // Calling withInferredReturnType(VOID_TYPE) should lead to a void return too.
  FunctionBuilder builder = new FunctionBuilder(registry)
          .withParams(new FunctionParamBuilder(registry))
          .withInferredReturnType(registry.getNativeType(JSTypeNative.VOID_TYPE));
  FunctionType ft = builder.build();
  assertEquals("function (): undefined", ft.toString());
  assertEquals(registry.getNativeType(JSTypeNative.VOID_TYPE), ft.getReturnType());
  }
  @Test
  public void testConstructorReturnTypeIsVoid_FunctionBuilder() {
  // A constructor with no return annotation must also default to void.
  FunctionBuilder builder = new FunctionBuilder(registry)
          .withParams(new FunctionParamBuilder(registry))
          .forConstructor();
  FunctionType ft = builder.build();
  assertTrue(ft.isConstructor());
  assertEquals("function (): undefined", ft.toString());
  assertEquals(registry.getNativeType(JSTypeNative.VOID_TYPE), ft.getReturnType());
  }
  // ----------------------------------------------------------------------------
  //  Integration tests via Compiler + TypedScopeCreator
  // ----------------------------------------------------------------------------
  /**
  - Helper: compile code and return the FunctionType of the top-level function named fnName.
    */
   private FunctionType getFunctionType(String code, String fnName) {
   SourceFile[] inputs = { SourceFile.fromCode("test.js", code) };
   SourceFile[] externs = {};  // minimal – ex‑IDE mode allows empty externs
   compiler.compile(externs, inputs, new PrintStreamErrorReporter(System.err));
   Scope topScope = compiler.getTopScope();
   assertNotNull("Top scope was null after compilation", topScope);
   Var var = topScope.getVar(fnName);
   assertNotNull("Variable '" + fnName + "' not found in top scope", var);
   JSType type = var.getType();
   assertNotNull("Type of '" + fnName + "' is null", type);
   return type.toMaybeFunctionType();
   }
  @Test
  public void testFunctionWithoutAnnotation_Compiler() {
      String code = "function f() {}";
      FunctionType ft = getFunctionType(code, "f");
      assertNotNull(ft);
      assertTrue("Expected return type to be void", ft.getReturnType().isVoidType());
      assertEquals("function (): undefined", ft.toString());
  }
  @Test
  public void testFunctionWithReturnStatementNoAnnotation_Compiler() {
      // Even when the body contains a return, the absence of a JSDoc annotation
      // means the return type must be void (undefined).
      String code = "function g() { return 42; }";
      FunctionType ft = getFunctionType(code, "g");
      assertNotNull(ft);
      assertTrue("Expected return type to be void", ft.getReturnType().isVoidType());
      assertEquals("function (): undefined", ft.toString());
  }
  @Test
  public void testConstructorWithoutAnnotation_Compiler() {
      // A constructor with no @return annotation must have return type undefined.
      String code = "/** @constructor
  */ function Bar() {}";
      FunctionType ft = getFunctionType(code, "Bar");
      assertNotNull(ft);
      assertTrue(ft.isConstructor());
      assertTrue("Expected return type to be void", ft.getReturnType().isVoidType());
      assertEquals("function (this:Bar): undefined", ft.toString());
  }
  @Test
  public void testFunctionWithExplicitUnknownReturn_Compiler() {
      // Explicit @return {?} must remain "?".
      String code = "/** @return {?}
  */ function h() {}";
      FunctionType ft = getFunctionType(code, "h");
      assertNotNull(ft);
      assertEquals(registry.getNativeType(JSTypeNative.UNKNOWN_TYPE), ft.getReturnType());
      assertEquals("function (): ?", ft.toString());
  }
  @Test
  public void testFunctionWithExplicitVoidReturn_Compiler() {
      // Explicit @return {void} must remain undefined.
      String code = "/** @return {void}
  */ function i() {}";
      FunctionType ft = getFunctionType(code, "i");
      assertNotNull(ft);
      assertEquals(registry.getNativeType(JSTypeNative.VOID_TYPE), ft.getReturnType());
      assertEquals("function (): undefined", ft.toString());
  }
  @Test
  public void testReturnTypeIsNotUnknownByDefault_Compiler() {
      // Sanity check: the default return type is NOT the unknown type.
      String code = "function noReturn() {}";
      FunctionType ft = getFunctionType(code, "noReturn");
      assertNotNull(ft);
      assertFalse("Default return type must not be unknown",
              ft.getReturnType().isUnknownType());
      assertTrue("Default return type must be void",
              ft.getReturnType().isVoidType());
  }

 }
