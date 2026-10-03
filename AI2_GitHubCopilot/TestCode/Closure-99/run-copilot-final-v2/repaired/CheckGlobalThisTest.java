package com.google.javascript.jscomp;

 import junit.framework.TestCase;
 import org.junit.Test;

 public class CheckGlobalThisTest extends TestCase {

   @Test public void testPropertyOfMethod() {
     testWarning("var obj={};obj.m=function(){this.x=1;}",
         CheckGlobalThis.GLOBAL_THIS);
   }

   @Test public void testMethod4() {
     testSame("/** @this {Element} */ function f() { this.innerHTML = 'hello'; }");
   }

   @Test public void testInterface1() {
     testSame("/** @interface */ function I() {}; I.prototype.m = function() { this.x = 1; };");
   }

   @Test public void testPlainFunction() {
     testWarning("function f() { this.x = 1; }", CheckGlobalThis.GLOBAL_THIS);
   }

   @Test public void testThisOnLeftSideOfAssignment() {
     testWarning("var a = this;", CheckGlobalThis.GLOBAL_THIS);
   }

   @Test public void testNestedFunction() {
     testWarning("function f() { function g() { this.y = 2; } }",
         CheckGlobalThis.GLOBAL_THIS);
   }

   @Test public void testConstructor() {
     testSame("/** @constructor */ function C() { this.attr = 5; }");
   }

   @Test public void testOverride() {
     testSame("/** @override */ function f() { this.x = 1; }");
   }

   @Test public void testAtThisAnnotationOnAssignment() {
     testSame("/** @this {Object} */ var f = function() { this.x = 1; };");
   }

   @Test public void testPrototypeMethod() {
     testSame("x.prototype.m = function() { this.x = 1; };");
   }

   @Test public void testObjectLiteralMethod() {
     testSame("var a = { m: function() { this.x = 1; } };");
   }

   @Test public void testPlainThisWithoutPropertyAccess() {
     testSame("function f() { return this; }");
   }

   private void testWarning(String js, DiagnosticType warning) {
     Compiler compiler = compile(js);
     JSError[] warnings = compiler.getWarnings();
     assertEquals("There should be one error.", 1, warnings.length);
     assertEquals(warning, warnings[0].getType());
   }

   private void testSame(String js) {
     Compiler compiler = compile(js);
     JSError[] warnings = compiler.getWarnings();
     JSError[] errors = compiler.getErrors();
     assertEquals("Unexpected error(s): " +
         (warnings.length > 0 ? warnings[0].toString() : ""),
         0, warnings.length + errors.length);
   }

   private Compiler compile(String js) {
     Compiler compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     options.checkGlobalThisLevel = CheckLevel.WARNING;
     JSSourceFile extern = JSSourceFile.fromCode("externs", "");
     JSSourceFile input = JSSourceFile.fromCode("testcode", js);
     compiler.compile(new JSSourceFile[]{extern}, new JSSourceFile[]{input}, options);
     return compiler;
   }
 }
