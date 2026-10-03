package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.ClosureCodingConvention;
 import com.google.javascript.jscomp.SourceFile;
 import com.google.javascript.jscomp.Scope;
 import com.google.javascript.jscomp.Scope.Var;
 import com.google.javascript.rhino.jstype.JSType;
 import com.google.javascript.rhino.jstype.ObjectType;
 import com.google.javascript.rhino.Node;

 import java.util.List;
 import java.util.ArrayList;

 /**
  * Tests that ScopedAliases correctly resolves forward JSDoc type references,
  * and that Scope.Var.toString() and getVar() behave as expected after alias resolution.
  * The main bug is that alias names used in JSDoc before their definition are not
  * replaced with the fully qualified name, causing Var type information to be incorrect.
  */
 public class ScopedAliasesForwardJsDocTest extends TestCase {

     private Compiler compiler;
     private CompilerOptions options;

     @Override
     protected void setUp() {
         compiler = new Compiler();        options = new CompilerOptions();
         options.setCodingConvention(new ClosureCodingConvention());
     }

     /**
      * Helper: compile a single source file and return the top-level scope.
      */
     private Scope compileAndGetGlobalScope(String src) {
         List<SourceFile> inputs = new ArrayList<SourceFile>();
         inputs.add(SourceFile.fromCode("testcode", src));

         // Minimal externs for basic runtime (goog, Object, etc.)
         String externs = "/** @const */ var goog = {};";
         List<SourceFile> externFiles = new ArrayList<SourceFile>();
         externFiles.add(SourceFile.fromCode("externs", externs));

         compiler.init(externFiles, inputs, options);
         compiler.parse();        compiler.check();
         compiler.process(); // runs ScopedAliases among others

         return compiler.getTopScope();
     }

     /**
      * Main bug: forward JSDoc alias usage should resolve to qualified name after pass.
      */
     public void testForwardJsDocAliasResolvesCorrectly() {
         String src =
             "goog.scope(function() {\n" +
             "  var /** !myAlias */ x = 1;\n" +          // forward JSDoc type
             "  var myAlias = goog.type.MyClass;\n" +     // alias definition
             "});\n" +
             "goog.type = {};\n" +
             "goog.type.MyClass = function() {};";

         Scope globalScope = compileAndGetGlobalScope(src);
         Var xVar = globalScope.getVar("x");
         assertNotNull("Variable 'x' should exist", xVar);

         JSType type = xVar.getType();
         assertNotNull("'x' should have a type", type);
         String typeStr = type.toString();

         // The type must be the resolved qualified name, not the local alias.
         assertTrue("Type should contain 'goog.type.MyClass' but was: " + typeStr,
                    typeStr.contains("goog.type.MyClass"));
         assertFalse("Type should not contain alias 'myAlias'",
                     typeStr.contains("myAlias"));
     }

     /**
      * Non-forward alias: JSDoc usage after definition should also resolve correctly.
      */
     public void testJsDocAliasAfterDefinition() {
         String src =
             "goog.scope(function() {\n" +
             "  var myAlias = goog.type.MyClass;\n" +
             "  var /** !myAlias */ x = 1;\n" +
             "});\n" +
             "goog.type = {};\n" +
             "goog.type.MyClass = function() {};";

         Scope globalScope = compileAndGetGlobalScope(src);
         Var xVar = globalScope.getVar("x");
         assertNotNull(xVar);
         JSType type = xVar.getType();
         assertNotNull(type);
         assertTrue("Type should resolve to qualified name",
                    type.toString().contains("goog.type.MyClass"));
     }

     /**
      * Nested alias chain: a -> b -> c, and JSDoc uses 'c', should resolve to deepest qualified
name.
      */
     public void testNestedAliasChainResolves() {
         String src =
             "goog.scope(function() {\n" +
             "  var /** !dc */ x = 1;\n" +
             "  var da = goog.foo.bar;\n" +
             "  var dc = da.baz;\n" +
             "});\n" +
             "goog.foo = {};\n" +
             "goog.foo.bar = {};\n" +
             "goog.foo.bar.baz = function() {};";

         Scope globalScope = compileAndGetGlobalScope(src);
         Var xVar = globalScope.getVar("x");
         assertNotNull(xVar);
         JSType type = xVar.getType();
         assertNotNull(type);
         assertTrue("Nested alias should resolve to full path",
                    type.toString().contains("goog.foo.bar.baz"));
     }

     /**
      * Alias used in a different scope (not scoped) should not be affected.
      * Here a global alias is defined outside goog.scope and is referenced inside.
      */
     public void testCrossScopeAliasUnchanged() {
         String src =
             "var globalAlias = goog.global.Type;\n" +
             "goog.scope(function() {\n" +
             "  var /** !globalAlias */ x = 1;\n" +
             "  var localAlias = goog.local.Other;\n" +
             "});\n" +
             "goog.global = {};\n" +
             "goog.global.Type = function() {};\n" +
             "goog.local = {};\n" +
             "goog.local.Other = function() {};";

         Scope globalScope = compileAndGetGlobalScope(src);
         Var xVar = globalScope.getVar("x");
         assertNotNull(xVar);
         JSType type = xVar.getType();
         assertNotNull(type);
         // The global alias may remain unresolved or be resolved; we primarily verify that
         // the local alias 'localAlias' does not pollute the global scope.
         // At minimum, the type should not contain the local alias name.
         assertFalse("Global alias resolution should not be contaminated by local alias",
                     type.toString().equals("localAlias"));
     }

     /**
      * Missing alias: using an undefined alias in JSDoc should result in an error or unknown type.
      * The compilation should still complete and the type should not crash.
      */
     public void testMissingAliasYieldsUnknownType() {
         String src =
             "goog.scope(function() {\n" +
             "  var /** !undefinedAlias */ x = 1;\n" +
             "});";

         // This may produce warnings; ignore them, just check that x exists.
         Scope globalScope = compileAndGetGlobalScope(src);
         Var xVar = globalScope.getVar("x");
         assertNotNull("Variable 'x' should be declared even with unknown alias", xVar);
         JSType type = xVar.getType();
         assertNotNull(type);
         // The type might be the placeholder Unknown, or attempt to resolve to undefinedAlias.
         // We just assert that it is not null and not the empty string.
         assertTrue("Type should be non-empty", type.toString().length() > 0);
     }

     /**
      * Alias used in return type JSDoc should be resolved.
      */
     public void testAliasInReturnType() {
         String src =
             "goog.scope(function() {\n" +
             "  var myAlias = goog.type.RetClass;\n" +            "  /** @return {!myAlias} */\n" +
             "  function foo() { return new goog.type.RetClass(); }\n" +
             "});\n" +
             "goog.type = {};\n" +
             "goog.type.RetClass = function() {};";

         Scope globalScope = compileAndGetGlobalScope(src);
         Var fooVar = globalScope.getVar("foo");
         assertNotNull(fooVar);
         JSType type = fooVar.getType();
         assertNotNull(type);
         assertTrue("Function 'foo' return type should be resolved",
                    type.toString().contains("RetClass"));
     }

     /**
      * Alias used in parameter JSDoc should be resolved.
      */
     public void testAliasInParamType() {
         String src =
             "goog.scope(function() {\n" +
             "  var myAlias = goog.type.ParamClass;\n" +
             "  /** @param {!myAlias} p */\n" +
             "  function foo(p) {}\n" +
             "});\n" +
             "goog.type = {};\n" +
             "goog.type.ParamClass = function() {};";

         Scope globalScope = compileAndGetGlobalScope(src);
         Var fooVar = globalScope.getVar("foo");
         assertNotNull(fooVar);
         JSType type = fooVar.getType();
         assertNotNull(type);
         assertTrue("Param type should be resolved",
                    type.toString().contains("ParamClass"));
     }

     /**
      * Var.getSourceFile() should return a valid file after alias processing.
      */
     public void testVarSourceFileAfterAliasProcessing() {
         String src =
             "goog.scope(function() {\n" +
             "  var myAlias = goog.type.SrcClass;\n" +
             "  var /** !myAlias */ x = 1;\n" +
             "});\n" +
             "goog.type = {};\n" +
             "goog.type.SrcClass = function() {};";

         Scope globalScope = compileAndGetGlobalScope(src);
         Var xVar = globalScope.getVar("x");
         assertNotNull(xVar);
         assertNotNull("Source file should not be null", xVar.getSourceFile());
         assertEquals("Source file name should be 'testcode'",
                      "testcode", xVar.getSourceFile().getName());
     }

     /**
      * Var.toString() should include the type information (the format may change,
      * but existence of type is essential).
      */
     public void testVarToStringContainsType() {
         String src =
             "goog.scope(function() {\n" +
             "  var MyAlias = goog.type.ToStrClass;\n" +
             "});\n" +
             "goog.type = {};\n" +            "goog.type.ToStrClass = function() {};";

         Scope globalScope = compileAndGetGlobalScope(src);
         Var aliasVar = globalScope.getVar("MyAlias");
         assertNotNull(aliasVar);
         String str = aliasVar.toString();
         assertNotNull(str);
         assertTrue("Var.toString should contain type info", str.contains("ToStrClass"));
     }

     /**
      * Forward alias in JSDoc with nested scope: alias defined after first usage in sibling
      * scope, must still resolve inside that scope.
      */
     public void testForwardAliasInNestedScope() {
         String src =
             "goog.scope(function() {\n" +
             "  if (true) {\n" +
             "    var /** !fwd */ a = 1;\n" +
             "  }\n" +
             "  var fwd = goog.type.Forwarded;\n" +
             "});\n" +
             "goog.type = {};\n" +
             "goog.type.Forwarded = function() {};";

         Scope globalScope = compileAndGetGlobalScope(src);
         Var aVar = globalScope.getVar("a");
         assertNotNull(aVar);
         JSType type = aVar.getType();
         assertNotNull(type);
         assertTrue("Forward alias should resolve across sibling scopes",
                    type.toString().contains("goog.type.Forwarded"));
     }

     /**
      * Multiple forward aliases used in the same scope.
      */
     public void testMultipleForwardAliases() {
         String src =
             "goog.scope(function() {\n" +
             "  var /** !f1 */ x = 1;\n" +
             "  var /** !f2 */ y = 2;\n" +
             "  var f1 = goog.type.First;\n" +
             "  var f2 = goog.type.Second;\n" +
             "});\n" +
             "goog.type = {};\n" +
             "goog.type.First = function() {};\n" +
             "goog.type.Second = function() {};";

         Scope globalScope = compileAndGetGlobalScope(src);
         Var xVar = globalScope.getVar("x");
         Var yVar = globalScope.getVar("y");
         assertNotNull(xVar); assertNotNull(yVar);
         assertTrue("First alias resolved", xVar.getType().toString().contains("First"));
         assertTrue("Second alias resolved", yVar.getType().toString().contains("Second"));
     }
 }