package com.google.javascript.jscomp;

import com.google.javascript.jscomp.CompilerTestCase;
import com.google.javascript.jscomp.CompilerPass;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.JSModule;
import com.google.javascript.jscomp.SourceFile;

/**

 - Unit tests for {@link ProcessClosurePrimitives}.
 - Covers normal, boundary, and error cases, with emphasis on
 - independent-module provides (Bug 261).
  */
 public class ProcessClosurePrimitivesTest extends CompilerTestCase {

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new ProcessClosurePrimitives(compiler, CheckLevel.ERROR, false); }

  @Override protected int getNumRepetitions() {
    return 1; }

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

  private JSModule createModule(String name, String code) {
    JSModule m = new JSModule(name);
    m.add(SourceFile.fromCode(name + ".js", code));
    return m; }
}