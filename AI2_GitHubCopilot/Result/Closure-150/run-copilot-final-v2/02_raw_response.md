package com.google.javascript.jscomp;

 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import com.google.javascript.rhino.jstype.FunctionType;
 import com.google.javascript.rhino.jstype.JSType;
 import com.google.javascript.rhino.jstype.ObjectType;

 import junit.framework.TestCase;

 /**
  * Targeted tests for TypedScopeCreator bug 150: stubs in local scopes
  * (namespaced function stubs and collected function stubs) fail to
  * resolve or carry the correct type.
  */
 public class TypedScopeCreatorBug150Test extends TestCase {

   private Compiler compiler;

   @Override
   protected void setUp() {
     compiler = new Compiler();
     compiler.init(new CompilerOptions());
   }

   /**
    * Builds the global scope for a JS snippet.
    */
   private Scope createGlobalScope(String js) {
     Node root = compiler.parseSyntheticCode("test.js", js);
     TypedScopeCreator creator = new TypedScopeCreator(compiler);
     return creator.createScope(root, null);
   }

   /**
    * Builds a local scope for the first function node inside the given JS.
    */
   private Scope createFirstLocalScope(String js) {
     Node root = compiler.parseSyntheticCode("test.js", js);
     TypedScopeCreator creator = new TypedScopeCreator(compiler);
     Scope globalScope = creator.createScope(root, null);
     Node func = findNode(root, Token.FUNCTION);
     if (func == null) {
       fail("No function found in source: " + js);
     }
     return creator.createScope(func, globalScope);
   }

   private Node findNode(Node n, int type) {
     if (n.getType() == type) return n;
     for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
       Node found = findNode(c, type);
       if (found != null) return found;
     }
     return null;
   }

   // -----------------------------------------------------------------
   //  stubs in local scopes
   // -----------------------------------------------------------------

   /** namespaced stub: a.b = function(){} inside a function */
   public void testNamespacedFunctionStubLocal() {
     Scope local = createFirstLocalScope(
         "function outer() { a.b = function() {}; }");
     Var var = local.getVar("a.b");
     assertNotNull("a.b should be declared in local scope", var);
     JSType type = var.getType();
     assertNotNull("a.b should have a type", type);
     assertTrue("a.b should be a function type",
         type instanceof FunctionType);
   }

   /** collected stub: inner named function */
   public void testCollectedFunctionStubLocal() {
     Scope local = createFirstLocalScope(
         "function outer() { function inner() {} }");
     Var var = local.getVar("inner");
     assertNotNull("inner should be declared in local scope", var);
     JSType type = var.getType();
     assertNotNull("inner should have a type", type);
     assertTrue("inner should be a function type",
         type instanceof FunctionType);
   }

   /** deeper qualified name: a.b.c = function(){} */
   public void testNestedNamespacedStub() {
     Scope local = createFirstLocalScope(
         "function outer() { a.b.c = function() {}; }");
     Var var = local.getVar("a.b.c");
     assertNotNull(var);
     JSType type = var.getType();
     assertNotNull(type);
     assertTrue(type instanceof FunctionType);
   }

   /** namespaced stub with a @type annotation */
   public void testNamespacedStubWithJSDoc() {
     Scope local = createFirstLocalScope(
         "/** @type {function():number} */ function outer() { a.b = function() {}; }"
     );
     Var var = local.getVar("a.b");
     assertNotNull(var);
     JSType type = var.getType();
     assertNotNull(type);
     // must carry function type (annotation may refine return type)
     assertTrue(type instanceof FunctionType);
   }

   /** stub in a nested local scope (function inside function) */
   public void testStubInNestedLocalScope() {
     Node root = compiler.parseSyntheticCode("test.js",
         "function outer() { function inner() { a.b = function() {}; } }");
     TypedScopeCreator creator = new TypedScopeCreator(compiler);
     Scope global = creator.createScope(root, null);
     Node outerFn = findNode(root, Token.FUNCTION);
     assertNotNull(outerFn);
     Scope outerScope = creator.createScope(outerFn, global);
     Node innerFn = findNode(outerFn.getLastChild(), Token.FUNCTION);
     assertNotNull(innerFn);
     Scope innerScope = creator.createScope(innerFn, outerScope);
     Var var = innerScope.getVar("a.b");
     assertNotNull(var);
     JSType type = var.getType();
     assertNotNull(type);
     assertTrue(type instanceof FunctionType);
   }

   /** multiple stubs in the same local scope */
   public void testMultipleStubsInLocalScope() {
     Scope local = createFirstLocalScope(
         "function outer() { a.b = function() {}; a.c = function() {}; }"
     );
     Var varB = local.getVar("a.b");
     assertNotNull(varB);
     assertTrue(varB.getType() instanceof FunctionType);
     Var varC = local.getVar("a.c");
     assertNotNull(varC);
     assertTrue(varC.getType() instanceof FunctionType);
   }

   /** stub assigned as a property of a local variable */
   public void testStubOnLocalObject() {
     Scope local = createFirstLocalScope(
         "function outer() { var x = {}; x.y = function() {}; }"
     );
     Var var = local.getVar("x.y");
     assertNotNull(var);
     JSType type = var.getType();
     assertNotNull(type);
     assertTrue(type instanceof FunctionType);
   }

   /** namespaced stub on 'this' */
   public void testNamespacedStubOnThis() {
     Scope local = createFirstLocalScope(
         "function outer() { this.x = function() {}; }"
     );
     Var var = local.getVar("this.x");
     assertNotNull(var);
     JSType type = var.getType();
     assertNotNull(type);
     assertTrue(type instanceof FunctionType);
   }

   /** var assigned a function expression should also be typed correctly */
   public void testLocalVarAssignedFunctionExpression() {
     Scope local = createFirstLocalScope(
         "function outer() { var f = function() { return 1; }; }"
     );
     Var var = local.getVar("f");
     assertNotNull(var);
     JSType type = var.getType();
     assertNotNull(type);
     assertTrue(type instanceof FunctionType);
   }

   /** stub in global scope should work (regression guard) */
   public void testNamespacedFunctionStubGlobal() {
     Scope global = createGlobalScope(
         "a.b = function() {};"
     );
     Var var = global.getVar("a.b");
     assertNotNull(var);
     JSType type = var.getType();
     assertNotNull(type);
     assertTrue(type instanceof FunctionType);
   }

   /** collected stub in global scope (named function) */
   public void testCollectedFunctionStubGlobal() {
     Scope global = createGlobalScope(
         "function foo() {}"
     );
     Var var = global.getVar("foo");
     assertNotNull(var);
     JSType type = var.getType();
     assertNotNull(type);
     assertTrue(type instanceof FunctionType);
   }

   /** stub with recursive self-reference pattern (not common but should not crash) */
   public void testRecursiveNamespacedStub() {
     Scope local = createFirstLocalScope(
         "function outer() { a = a || {}; a.b = function() { return a.b; }; }"
     );
     Var var = local.getVar("a.b");
     assertNotNull(var);
     JSType type = var.getType();
     assertNotNull(type);
     assertTrue(type instanceof FunctionType);
   }
 }