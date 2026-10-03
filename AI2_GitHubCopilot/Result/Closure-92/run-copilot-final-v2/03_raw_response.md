package com.google.javascript.jscomp;

import junit.framework.TestCase;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.JSModule;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.ProcessClosurePrimitives;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.DiagnosticType;
import com.google.javascript.jscomp.JSError;

/**

 - Unit tests for {@link ProcessClosurePrimitives}.
 - Covers normal, boundary, and error cases, with emphasis on
 - independent-module provides (Bug 261).
  */
 public class ProcessClosurePrimitivesTest extends TestCase {

  private ProcessClosurePrimitives getProcessor(Compiler compiler) {
    return new ProcessClosurePrimitives(compiler, CheckLevel.ERROR, false); }

  private void test(String js, String expected) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.initOptions(options);
    SourceFile extern = SourceFile.fromCode("externs", "");
    SourceFile input = SourceFile.fromCode("testcode", js);
    compiler.compile(extern, input, options);
    ProcessClosurePrimitives pass = getProcessor(compiler);
    pass.process(null, compiler.getRoot());
    assertEquals(expected, compiler.toSource()); }

  private void testError(String js, DiagnosticType expectedError) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.initOptions(options);
    SourceFile extern = SourceFile.fromCode("externs", "");
    SourceFile input = SourceFile.fromCode("testcode", js);
    compiler.compile(extern, input, options);
    ProcessClosurePrimitives pass = getProcessor(compiler);
    pass.process(null, compiler.getRoot());
    JSError[] errors = compiler.getErrors();
    boolean found = false;
    for (JSError e : errors) {
      if (e.getType().equals(expectedError)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected error not found: " + expectedError, found); }

  private void test(JSModule[] modules, String[] expected) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.initOptions(options);
    compiler.compile(modules, options);
    ProcessClosurePrimitives pass = getProcessor(compiler);
    pass.process(null, compiler.getRoot());
    for (int i = 0; i < modules.length; i++) {
      assertEquals(expected[i], compiler.toSource(modules[i]));
    } }

  private JSModule createModule(String name, String code) {
    JSModule m = new JSModule(name);
    m.add(SourceFile.fromCode(name + ".js", code));
    return m; }

  /**

 - Normal case: a single goog.provide should create a variable declaration
 - for the provided namespace.
    */
   public void testProvideSimple() {
 test("goog.provide('foo');", "var foo=foo||{};");
   }

  /**

 - Nested namespace: goog.provide('a.b.c') should create the root object
 - and then assign the nested property.
    */
   public void testProvideNestedNamespace() {
 test("goog.provide('a.b.c');",
 "var a=a||{};a.b=a.b||{};a.b.c=a.b.c||{};");

  }

  /**

 - Function declaration shadowing a provided name is not allowed.
    */
   public void testFunctionDeclarationWithProvidedNameError() {
 testError("goog.provide('Foo'); function Foo() {}",
      ProcessClosurePrimitives.FUNCTION_NAMESPACE_ERROR);

  }

  /**

 - Independent modules that each provide the same namespace must both
 - receive their own declaration node.  This is the critical case for
 - Bug 261 (testProvideInIndependentModules4).
    */
   public void testProvideInIndependentModules4() {
 JSModule mod1 = createModule("m1", "goog.provide('ns');");
 JSModule mod2 = createModule("m2", "goog.provide('ns');");
 test(new JSModule[] { mod1, mod2 },
 new String[] { "var ns=ns||{};", "var ns=ns||{};" });

  }

  /**

 - More than two independent modules providing the same namespace.
    */
   public void testProvideInThreeIndependentModules() {
 JSModule m1 = createModule("m1", "goog.provide('shared');");
 JSModule m2 = createModule("m2", "goog.provide('shared');");
 JSModule m3 = createModule("m3", "goog.provide('shared');");
 test(new JSModule[] { m1, m2, m3 },
 new String[] {
     "var shared=shared||{};",
     "var shared=shared||{};",
     "var shared=shared||{};"
 });

  }
}