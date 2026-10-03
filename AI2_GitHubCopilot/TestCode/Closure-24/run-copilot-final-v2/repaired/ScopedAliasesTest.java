package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.CheckLevel;
 import com.google.javascript.jscomp.DiagnosticGroups;
 import com.google.javascript.jscomp.DiagnosticType;
 import com.google.javascript.jscomp.JSError;
 import com.google.javascript.jscomp.ScopedAliases;
 import com.google.javascript.jscomp.SourceFile;
 import junit.framework.TestCase;

 /**
  * Tests for ScopedAliases pass focusing on detection of non-alias locals
  * and validation rules inside goog.scope blocks.
  */
 public class ScopedAliasesTest extends TestCase {

     private Compiler compiler;
     private static final String EXTERNS =
             "var goog = {};" +
             "goog.scope = function(f) {};" +
             "goog.dom = {};" +
             "goog.dom.createElement = function(tag) {};" +
             "goog.events = {};" +
             "goog.events.listen = function(src, type, fn) {};";

     @Override
     protected void setUp() throws Exception {
         super.setUp();
         compiler = new Compiler();
         compiler.disableThreads();
     }

     private void compile(String code) {
         SourceFile externsFile = SourceFile.fromCode("externs", EXTERNS);
         SourceFile inputFile = SourceFile.fromCode("input", code);
         CompilerOptions options = new CompilerOptions();
         options.setWarningLevel(DiagnosticGroups.SCOPED_ALIASES, CheckLevel.ERROR);
         compiler.compile(externsFile, inputFile, options);
     }

     /**
      * A non-alias local variable declaration (e.g. var x = 1;) inside
      * goog.scope must produce a GOOG_SCOPE_NON_ALIAS_LOCAL error.
      * This is the main bug scenario: the error is expected but was missing.
      */
     public void testNonAliasLocal() {
         compile("goog.scope(function() { var x = 1; });");
         assertEquals("Should have exactly one error", 1,
                 compiler.getErrorCount());
         JSError error = compiler.getErrors()[0];
         assertTrue("Error should mention non-alias",
                 error.description.contains("non-alias"));
     }

     /**
      * A local variable without any initializer is also not an alias
      * and should be rejected.
      */
     public void testNonAliasLocalNoInitializer() {
         compile("goog.scope(function() { var x; });");
         assertEquals(1, compiler.getErrorCount());
     }

     /**
      * A valid alias (RHS is a qualified name) and its usage should produce
      * zero errors.
      */
     public void testValidAlias() {
         compile("goog.scope(function() {" +
                 "  var dom = goog.dom;" +
                 "  dom.createElement('div');" +
                 "});");
         assertEquals(0, compiler.getErrorCount());
     }

     /**
      * Redefining an alias with the same name inside the same goog.scope
      * block triggers GOOG_SCOPE_ALIAS_REDEFINED.
      */
     public void testAliasRedefined() {
         compile("goog.scope(function() {" +
                 "  var a = goog.dom;" +
                 "  var a = goog.events;" +
                 "});");
         assertEquals(1, compiler.getErrorCount());
         JSError error = compiler.getErrors()[0];
         assertTrue(error.description.contains("redefined"));
     }

     /**
      * Calling goog.scope in any non-expression-result position is improper.
      */
     public void testGoogScopeUsedImproperly() {
         compile("var x = goog.scope(function() {" +
                 "  var dom = goog.dom;" +
                 "});");
         assertEquals(1, compiler.getErrorCount());
         JSError error = compiler.getErrors()[0];
         assertTrue(error.description.contains("improper"));
     }

     /**
      * goog.scope must receive exactly one argument which is an anonymous
      * function with no parameters.
      */
     public void testGoogScopeBadParametersNoArgs() {
         compile("goog.scope();");
         assertEquals(1, compiler.getErrorCount());
     }

     public void testGoogScopeBadParametersWrongArg() {
         compile("goog.scope(42);");
         assertEquals(1, compiler.getErrorCount());
     }

     /**
      * Using 'return' inside the goog.scope function body is forbidden.
      */
     public void testGoogScopeUsesReturn() {
         compile("goog.scope(function() { return; });");
         assertEquals(1, compiler.getErrorCount());
         JSError error = compiler.getErrors()[0];
         assertTrue(error.description.contains("return"));
     }

     /**
      * Using 'this' inside the goog.scope function body is forbidden.
      */
     public void testGoogScopeReferencesThis() {
         compile("goog.scope(function() { this; });");
         assertEquals(1, compiler.getErrorCount());
         JSError error = compiler.getErrors()[0];
         assertTrue(error.description.contains("this"));
     }

     /**
      * Using 'throw' inside the goog.scope function body is forbidden.
      */
     public void testGoogScopeUsesThrow() {
         compile("goog.scope(function() { throw new Error(); });");
         assertEquals(1, compiler.getErrorCount());
         JSError error = compiler.getErrors()[0];
         assertTrue(error.description.contains("throw"));
     }

     /**
      * Alias usage inside a nested function of the scope should still work.
      */
     public void testAliasInNestedFunction() {
         compile("goog.scope(function() {" +
                 "  var dom = goog.dom;" +
                 "  function inner() { dom.createElement('span'); }" +
                 "  inner();" +
                 "});");
         assertEquals(0, compiler.getErrorCount());
     }

     /**
      * An alias used as a JSDoc type expression (fixTypeNode case) should
      * not cause any error.
      */
     public void testAliasInTypeAnnotation() {
         compile("goog.scope(function() {" +
                 "  var dom = goog.dom;" +
                 "  /** @type {dom.Element} */" +
                 "  var el = null;" +
                 "});");
         assertEquals(0, compiler.getErrorCount());
     }

     /**
      * Transitive aliasing (var g = goog; var d = g.dom;) is allowed;
      * both are qualified-name-assigned aliases.
      */
     public void testTransitiveAlias() {
         compile("goog.scope(function() {" +
                 "  var g = goog;" +
                 "  var d = g.dom;" +
                 "  d.createElement('div');" +
                 "});");
         assertEquals(0, compiler.getErrorCount());
     }
 }
