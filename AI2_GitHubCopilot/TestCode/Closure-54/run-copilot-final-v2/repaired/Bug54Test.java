package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.CheckLevel;
 import com.google.javascript.jscomp.DiagnosticGroups;
 import org.junit.Test;

 public class Bug54Test extends CompilerTypeTestCase {

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     getOptions().setWarningLevel(DiagnosticGroups.MISSING_PROPERTIES,
         CheckLevel.WARNING);
   }

   @Test
   public void testKnownSuperclassArgMismatch() {
     testTypes(
         "Foo = function() {};" +
         "Foo.prototype.method = function() {};" +
         "/** @constructor @extends {Foo} */" +
         "Baz = function() { this.method(1); };",
         "Function Foo.prototype.method: called with 1 argument(s). Function requires at " +
         "least 0 argument(s) and no more than 0 argument(s).");
   }

   @Test
   public void testIssue537a() {
     testTypes(
         "Foo = function() {};" +
         "Foo.prototype.method = function() {};" +
         "/** @constructor @extends {Bar} */" +
         "Baz = function() { this.method(1); };",
         "Function Foo.prototype.method: called with 1 argument(s). Function requires at " +
         "least 0 argument(s) and no more than 0 argument(s).");
   }

   @Test
   public void testIssue537b() {
     testTypes(
         "Bar.prototype.baz = function() {};" +
         "/** @constructor @extends {Bar} */" +
         "C = function() { this.baz(1); };",
         "Function Bar.prototype.baz: called with 1 argument(s). Function requires at " +
         "least 0 argument(s) and no more than 0 argument(s).");
   }

   @Test
   public void testPropertyOnUnknownSuperClass2() {
     testTypes(
         "/** @constructor @extends {Unknown} */" +
         "C = function() { this.prop = 3; };",
         "?");
   }

   @Test
   public void testUnknownSuperclassThisType() {
     testTypes(
         "/** @constructor @extends {Unknown} */" +
         "D = function() { this.prop2 = 42; };",
         "?");
   }
 }
