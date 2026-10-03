package com.google.javascript.jscomp;

import com.google.common.collect.Lists;
import com.google.javascript.jscomp.BasicErrorManager;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.SourceFile;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**

 - Tests that {@link TypeCheck} emits a warning when the this argument
 - of a call (call/apply) is incompatible with the function's expected
 - this type.  All tests in this class expose the missing warning bug
 - (Defects4J Closures-69b) and will fail on the buggy compiler.
  */
 public class TypeCheckThisCompatibilityTest extends TestCase {

  /** An error manager that records all warnings in a list. */ private static class TestErrorManager
extends BasicErrorManager {
    public final List<JSError> warnings = new ArrayList<JSError>();

 @Override
 public void report(CheckLevel level, JSError error) {
   if (level == CheckLevel.WARNING) {
     warnings.add(error);
   }
 }

 @Override
 public void println(CheckLevel level, JSError error) {
   // no output
 }

 @Override
 public void printSummary() {
   // no output
 } }

  /**

 - Runs type checking on the given JavaScript source and returns the
 - corresponding error manager so that the number and content of warnings
 - can be inspected.
    /
   private TestErrorManager compile(String js) throws Exception {
 // Minimal externs that are necessary for the tested snippets.
 String externs =
    "/* @constructor / function Object() {}\n"
    + "/* @constructor / function Function() {}\n"
    + "/* @constructor / function Array() {}\n"
    + "/* @constructor / function String() {}\n"
    + "/* @constructor / function Number() {}\n"
    + "/* @constructor / function Boolean() {}\n"
    + "/* @constructor / function RegExp() {}\n"
    + "/* @constructor / function Error() {}\n"
    + "var undefined;\n"
    + "/* @param {=} thisArg @param {...} var_args @return {} /\n"
    + "Function.prototype.call = function(thisArg, var_args) {};\n"
    + "/ @param {=} thisArg @param {Array=} args @return {}
  */\n"
    + "Function.prototype.apply = function(thisArg, args) {};\n";

 List<SourceFile> externFiles = Lists.newArrayList(
     SourceFile.fromCode("externs.js", externs));
 List<SourceFile> jsFiles = Lists.newArrayList(
     SourceFile.fromCode("test.js", js));

 CompilerOptions options = new CompilerOptions();
 options.setCheckTypes(CheckLevel.WARNING);
 // Turn on all warnings so that the "this type" diagnostic is active.
 options.setWarningLevel(DiagnosticGroups.ALL, CheckLevel.WARNING);

 Compiler compiler = new Compiler();
 TestErrorManager em = new TestErrorManager();
 compiler.setErrorManager(em);
 compiler.compile(externFiles, jsFiles, options);

 return em; }

  // ---------- tests ----------

  /** Normal method call – this matches expectation, no warning. / public void
testCompatibleThisDirectCall() throws Exception {
    TestErrorManager em = compile(
        "/* @constructor */ function Foo(){};"
        + "Foo.prototype.m = function(){};"
        + "var o = new Foo(); o.m();");
    assertEquals("Expected no warning for correct this binding",
        0, em.warnings.size()); }

  /** call() with exactly the right this – no warning. / public void testCompatibleThisWithCall()
throws Exception {
    TestErrorManager em = compile(
        "/* @constructor */ function Bar(){};"
        + "Bar.prototype.m = function(){};"
        + "var b = new Bar(); b.m.call(b);");
    assertEquals("Expected no warning when this matches",
        0, em.warnings.size()); }

  /** call() with a plain object – this type is incompatible → warning. / public void
testIncompatibleThisWithPlainObject() throws Exception {
    TestErrorManager em = compile(
        "/* @constructor */ function Bar(){};"
        + "Bar.prototype.m = function(){};"
        + "var b = new Bar(); b.m.call({});");
    assertTrue("Expected a warning for incompatible this type",
        em.warnings.size() > 0); }

  /** call() with null this – null is not assignable to the expected type. / public void
testIncompatibleThisWithNull() throws Exception {
    TestErrorManager em = compile(
        "/* @constructor */ function Bar(){};"
        + "Bar.prototype.m = function(){};"
        + "var b = new Bar(); b.m.call(null);");
    assertTrue("Expected a warning when this is null",
        em.warnings.size() > 0); }

  /** call() with undefined this – undefined is not assignable. / public void
testIncompatibleThisWithUndefined() throws Exception {
    TestErrorManager em = compile(
        "/* @constructor */ function Bar(){};"
        + "Bar.prototype.m = function(){};"
        + "var b = new Bar(); b.m.call(undefined);");
    assertTrue("Expected a warning when this is undefined",
        em.warnings.size() > 0); }

  /** Prototype method called directly with a wrong this. / public void
testIncompatibleThisPrototypeDirect() throws Exception {
    TestErrorManager em = compile(
        "/* @constructor */ function Bar(){};"
        + "Bar.prototype.m = function(){};"
        + "Bar.prototype.m.call(42);");
    assertTrue("Expected a warning for wrong this on prototype method",
        em.warnings.size() > 0); }

  /** A function annotated with {@code @this} should warn when violated. / public void
testIncompatibleThisWithAnnotation() throws Exception {
    TestErrorManager em = compile(
        "/* @this {String} */ function foo(){};"
        + "foo.call({});");
    assertTrue("Expected a warning for mismatched @this annotation",
        em.warnings.size() > 0); }

  /** A global function without a this type constraint accepts anything. */ public void
testCompatibleThisOnGlobal() throws Exception {
    TestErrorManager em = compile(
        "function noop(){}; noop.call(42); noop.call(null);");
    assertEquals("Expected no warnings for unconstrained this",
        0, em.warnings.size()); }

  /** call() with the correct this after an implicit cast is still fine. / public void
testCompatibleThisWithImplicitCast() throws Exception {
    TestErrorManager em = compile(
        "/* @constructor */ function Baz(){};"
        + "Baz.prototype.m = function(this:Baz){};"
        + "var z = new Baz(); z.m.call(z);");
    assertEquals("Expected no warning when this matches exactly",
        0, em.warnings.size()); }

  /**

 - Even with a stricter this type (via the internal annotation)
 - the call should warn when the receiver type is wrong.
    /
   public void testIncompatibleThisWithExplicitThisType() throws Exception {
 TestErrorManager em = compile(
    "/* @constructor
  */ function Qux(){};"
  - "/** @this {Qux}
   */ Qux.prototype.m = function(){};"
  - "var q = new Qux(); q.m.call({});");
  assertTrue("Expected a warning for explicit @this mismatch",
     em.warnings.size() > 0);
    }
  }