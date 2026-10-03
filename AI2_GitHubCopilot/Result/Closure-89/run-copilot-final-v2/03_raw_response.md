package com.google.javascript.jscomp;

 /**
  * Tests for CollapseProperties that target the bug described in issue 289.
  * These tests verify correct inlining of aliases and collapsing of properties
  * in the presence of local scope functions, especially when a property is added
  * to an uncollapsible function or when an alias is created for a function at
  * depth 1 or 2.
  */
 public class CollapsePropertiesBugTest extends CompilerTestCase {

   @Override
   protected CompilerPass getProcessor(Compiler compiler) {
     return new CollapseProperties(compiler, false, true);
   }

   @Override
   protected int getNumRepetitions() {
     return 1;
   }

   public void testAddPropertyToChildOfUncollapsibleFunctionInLocalScope() {
     // Adding a property to an uncollapsible function inside a local scope
     // should leave the property access unchanged.
     test(
         "function f() {" +
         "  var ns = {};" +
         "  ns.F = function() {};" +
         "  ns.F.a = 1;" +
         "}",
         "function f() {" +
         "  var ns = {};" +
         "  ns.F = function() {};" +
         "  ns.F.a = 1;" +
         "}");
   }

   public void testAliasCreatedForFunctionDepth1_1() {
     // Alias of a function at depth 1 must be inlined so that calls
     // are rewritten directly.
     test(
         "var ns = {};" +
         "ns.f = function() {};" +
         "var alias = ns.f;" +
         "alias();",
         "var ns = {};" +
         "ns.f = function() {};" +
         "ns.f();");
   }

   public void testAliasCreatedForFunctionDepth1_2() {
     // Alias used to set a property: the set must be applied to the
     // original qualified name after inlining.
     test(
         "var ns = {};" +
         "ns.f = function() {};" +
         "var alias = ns.f;" +
         "alias.a = 1;" +
         "ns.f.a;",
         "var ns = {};" +
         "ns.f = function() {};" +
         "ns.f.a = 1;" +
         "ns.f.a;");
   }

   public void testAliasCreatedForFunctionDepth1_3() {
     // Alias used to set a prototype property after inlining.
     test(
         "var ns = {};" +
         "ns.f = function() {};" +
         "var alias = ns.f;" +
         "alias.prototype.b = 2;",
         "var ns = {};" +
         "ns.f = function() {};" +
         "ns.f.prototype.b = 2;");
   }

   public void testAddPropertyToUncollapsibleNamedCtorInLocalScopeDepth1() {
     // A named constructor function in a local scope is uncollapsible;
     // properties added to it must remain unmodified.
     test(
         "function outer() {" +
         "  function Ctor() {}" +
         "  Ctor.a = 1;" +
         "}",
         "function outer() {" +
         "  function Ctor() {}" +
         "  Ctor.a = 1;" +
         "}");
   }

   public void testAddPropertyToUncollapsibleFunctionInLocalScopeDepth1() {
     // Variable holding a function expression in local scope cannot be
     // collapsed, nor can properties attached to it.
     test(
         "function f() {" +
         "  var x = function() {};" +
         "  x.a = 1;" +
         "}",
         "function f() {" +
         "  var x = function() {};" +
         "  x.a = 1;" +
         "}");
   }

   public void testAddPropertyToUncollapsibleFunctionInLocalScopeDepth2() {
     // Depth-2 property access on an uncollapsible, locally-scoped object
     // that holds a function should not be collapsed.
     test(
         "function f() {" +
         "  var x = {};" +
         "  x.y = function() {};" +
         "  x.y.z = 2;" +
         "}",
         "function f() {" +
         "  var x = {};" +
         "  x.y = function() {};" +
         "  x.y.z = 2;" +
         "}");
   }

   public void testAliasCreatedForFunctionDepth2() {
     // Alias at depth 2 for a function must be inlined everywhere,
     // including property sets and calls.
     test(
         "var ns = {};" +
         "ns.a = {};" +
         "ns.a.b = function() {};" +
         "var alias = ns.a.b;" +
         "alias.c = 1;" +
         "alias();",
         "var ns = {};" +
         "ns.a = {};" +
         "ns.a.b = function() {};" +
         "ns.a.b.c = 1;" +
         "ns.a.b();");
   }
 }