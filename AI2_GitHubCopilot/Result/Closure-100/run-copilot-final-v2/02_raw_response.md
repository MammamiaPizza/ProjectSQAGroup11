package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.DiagnosticGroups;
 import com.google.javascript.jscomp.JSError;
 import com.google.javascript.jscomp.SourceFile;
 import com.google.javascript.rhino.Node;

 import junit.framework.TestCase;

 import java.util.List;

 /**
  * Tests for {@link CheckGlobalThis}. The bug (issue 144) causes incorrect
  * reporting of global {@code this} in static functions and inner functions.
  */
 public class CheckGlobalThisTest extends TestCase {

   private Compiler compiler;
   private CompilerOptions options;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     compiler = new Compiler();
     options = new CompilerOptions();
     options.setWarningLevel(DiagnoticGroups.GLOBAL_THIS, CheckLevel.WARNING);
   }

   private void assertWarningCount(String js, int expectedCount) {
     compiler.compile(
         new SourceFile[] { SourceFile.fromCode("externs", "") },
         new SourceFile[] { SourceFile.fromCode("testcode", js) },
         options);
     int count = 0;
     for (JSError warning : compiler.getWarnings()) {
       if (CheckGlobalThis.GLOBAL_THIS.key.equals(warning.getType().key)) {
         count++;
       }
     assertEquals(expectedCount, count);
   }

   // ---- static function with @this: expected no warning ----
   public void testStaticFunction6() {
     String js = "/** @this {Object} */ obj.method = function() { this.prop = 1; };";
     assertWarningCount(js, 0);
   }

   public void testStaticFunction7() {
     String js = "obj.method = / * @this {Object} */ function() { this.prop = 1; };";
     // JSDoc attached directly to the function expression
     assertWarningCount(js,0);
   }

   // static function without @this: expected 1 warnings
   public void testStaticFunction8() {
     String j = "obj.method = function() { this.prop = 1; };";
     assertWarningCount(j, 1);
   }

   // ---- global this (top-level) -----
   public void testGlobalThis7() {
     String s = "this.prop = 1;";
     assertWarningCount(s, 1);
   }

   // ---- static method variations ----
   public void testStaticMethod2() {
     // static method with @this on the var declaration containing the assignment
     String s = "/** @this {Object} */ var f = obj.method = function() { this.prop = 1; };";
     assertWarningCount(s, 0);
   }

   public void testStaticMethod3() {
     // static method that is a named function expression (not just anonymous)
     String s = "obj.method = function myMethod() { this.prop = 1; };";
     assertWarningCount(s, 1);
   }

   // ---- inner functions inside annotated/constructor functions: expected a warning ----
   public void testInnerFunction1() {
     String s = "/** @constructor */ function Foo() { function inner() { this.prop = 1; } }";
     assertWarningCount(s, 1);
   }

   public void testInnerFunction2() {
     String s = "/** @this {Object} */ function handler() { var inner = function() { this.prop = 1;
}; }";
     assertWarningCount(s, 1);
   }

   public void testInnerFunction3() {
     String s = "/** @this {Object} */ function outer() { (function () { this.prop = 1; })(); }";
     assertWarningCount(s, 1);
   }

   // ---- other common scenarios ----
   public void testPrototypeMethod() {
     // prototype methods are automatically excluded, so no warning
     String s = "Obj.prototype.method = function() { this.prop = 1; };";
     assertWarningCount(s, 0);
   }

   public void testConstructor() {
     String s = "/** @constructor */ function Foo() { this.prop = 1; }";
     assertWarningCount(s, 0);
   }
 }