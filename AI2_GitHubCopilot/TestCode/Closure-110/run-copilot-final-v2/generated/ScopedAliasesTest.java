package com.google.javascript.jscomp;

 public class ScopedAliasesTest extends CompilerTestCase {

     private static final String EXTERNS =
         "var goog = {};\n" +
         "goog.scope = function(fn) {};\n" +
         "goog.dom = {};\n" +
         "goog.dom.bar = function() {};\n" +
         "goog.array = {};\n" +
         "goog.array.forEach = function() {};\n" +
         "goog.events = {};\n" +
         "goog.events.listen = function() {};";

     public ScopedAliasesTest() {
         super(EXTERNS);
     }

     @Override
     protected CompilerPass getProcessor(Compiler compiler) {
         return new ScopedAliases(compiler, null,
             CompilerOptions.AliasTransformationHandler.NULL);
     }

     @Override
     protected int getNumRepetitions() {
         return 1;
     }

     @Override
     protected Compiler createCompiler() {
         return new Compiler();
     }

     /**
      * A plain function declaration inside goog.scope must not trigger
      * GOOG_SCOPE_NON_ALIAS_LOCAL. This is the core bug: function declarations
      * were incorrectly flagged as non-alias locals.
      */
     public void testFunctionDeclaration() {
         test(
             "goog.scope(function() {\n" +
             "  function f() {}\n" +
             "});",
             "function f() {}");
     }

     /**
      * A hoisted function declaration (used before its definition) inside
      * goog.scope must not trigger GOOG_SCOPE_NON_ALIAS_LOCAL either.
      */
     public void testHoistedFunctionDeclaration() {
         test(
             "goog.scope(function() {\n" +
             "  f();\n" +
             "  function f() {}\n" +
             "});",
             "f();\n" +
             "function f() {}");
     }

     /**
      * Multiple function declarations in the same goog.scope should all be
      * allowed without errors.
      */
     public void testMultipleFunctionDeclarations() {
         test(
             "goog.scope(function() {\n" +
             "  function f() {}\n" +
             "  function g() {}\n" +
             "  function h() {}\n" +
             "});",
             "function f() {}\n" +
             "function g() {}\n" +
             "function h() {}");
     }

     /**
      * A var assignment to a non-namespaced value (not an alias) must still
      * produce GOOG_SCOPE_NON_ALIAS_LOCAL.
      */
     public void testNonAliasVarAssignment() {
         testError(
             "goog.scope(function() {\n" +
             "  var f = 1;\n" +
             "});",
             ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
     }

     /**
      * A var assignment to a function expression (not a function declaration)
      * is still a non-alias local and must produce the error.
      */
     public void testFunctionExpressionAsVar() {
         testError(
             "goog.scope(function() {\n" +
             "  var f = function() {};\n" +
             "});",
             ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
     }

     /**
      * A var assignment to an object literal is not an alias and must error.
      */
     public void testObjectLiteralVarAssignment() {
         testError(
             "goog.scope(function() {\n" +
             "  var f = {};\n" +
             "});",
             ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
     }

     /**
      * A proper alias assignment (var x = goog.something) must not error.
      */
     public void testValidAliasAssignment() {
         test(
             "goog.scope(function() {\n" +
             "  var foo = goog.dom;\n" +
             "  foo.bar();\n" +
             "});",
             "goog.dom.bar();");
     }

     /**
      * Function declarations mixed with valid alias assignments must all work
      * without spurious NON_ALIAS_LOCAL errors.
      */
     public void testFunctionDeclarationMixedWithAlias() {
         test(
             "goog.scope(function() {\n" +
             "  var foo = goog.dom;\n" +
             "  foo.bar();\n" +
             "  function f() {}\n" +
             "});",
             "goog.dom.bar();\n" +
             "function f() {}");
     }

     /**
      * A function declaration that itself uses an alias should still be allowed
      * and the alias should be resolved inside the function body.
      */
     public void testFunctionDeclarationUsingAlias() {
         test(
             "goog.scope(function() {\n" +
             "  var foo = goog.dom;\n" +
             "  function f() {\n" +
             "    foo.bar();\n" +
             "  }\n" +
             "});",
             "function f() {\n" +
             "  goog.dom.bar();\n" +
             "}");
     }

     /**
      * Nested function declarations inside goog.scope must not produce errors.
      */
     public void testNestedFunctionDeclaration() {
         test(
             "goog.scope(function() {\n" +
             "  function outer() {\n" +
             "    function inner() {}\n" +
             "  }\n" +
             "});",
             "function outer() {\n" +
             "  function inner() {}\n" +
             "}");
     }

     /**
      * Function declarations placed both before and after an alias definition
      * must all be accepted without errors.
      */
     public void testFunctionsBeforeAndAfterAlias() {
         test(
             "goog.scope(function() {\n" +
             "  function f() {}\n" +
             "  var foo = goog.dom;\n" +
             "  foo.bar();\n" +
             "  function g() {}\n" +
             "});",
             "function f() {}\n" +
             "goog.dom.bar();\n" +
             "function g() {}");
     }

     /**
      * Multiple aliases together with function declarations must work without
      * errors. All aliases should be resolved and function declarations kept.
      */
     public void testMultipleAliasesWithFunctions() {
         test(
             "goog.scope(function() {\n" +
             "  var dom = goog.dom;\n" +
             "  var arr = goog.array;\n" +
             "  dom.bar();\n" +
             "  function f() {\n" +
             "    arr.forEach();\n" +
             "  }\n" +
             "});",
             "goog.dom.bar();\n" +
             "function f() {\n" +
             "  goog.array.forEach();\n" +
             "}");
     }
 }
