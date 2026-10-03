package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;

/**

 - Tests for NameAnalyzer focusing on correct handling of prototype chains,
 - aliases and class-defining functions (issue 284).
  */
 public class NameAnalyzerIssue284Test extends CompilerTestCase {

  @Override protected CompilerPass getProcessor(final Compiler compiler) {
    return new CompilerPass() {
      @Override
      public void process(Node externs, Node root) {
        NameAnalyzer analyzer = new NameAnalyzer(compiler, true);
        analyzer.process(externs, root);
      }
    }; }

  @Override protected int getNumRepetitions() {
    return 1; }

  /**

 - Issue 284: simple prototype chain must not cause an internal error.
    */
   public void testPrototypeChain() {
 testSame(
    "function Foo(){}" +
    "Foo.prototype.bar=function(){};" +
    "new Foo().bar()");
   }

  /**

 - Alias to the prototype object, then assignment through the alias.
    */
   public void testPrototypeAliasAssignment() {
 testSame(
    "function Foo(){}" +
    "Foo.prototype.bar=function(){};" +
    "var alias=Foo.prototype;" +
    "alias.baz=function(){};" +
    "new Foo().baz()");
   }

  /**

 - Hidden alias returned from an immediately-invoked function expression.
    */
   public void testHiddenAliasFromIIFE() {
 testSame(
    "function Foo(){}" +
    "var alias=(function(){return Foo.prototype})();" +
    "alias.m=function(){};" +
    "new Foo().m()");
   }

  /**

 - Class-defining function (inheritance) with a subsequent method definition.
    */
   public void testClassDefiningInheritance() {
 testSame(
    "function Parent(){}" +
    "function Child(){}" +
    "Child.prototype=new Parent();" +
    "Child.prototype.myMethod=function(){};" +
    "new Child().myMethod()");
   }

  /**

 - Unused top-level variable should be removed without errors.
    */
   public void testUnusedVariableRemoved() {
 test("var unused=1;function keep(){return keep}",
 "function keep(){return keep}");

  }

  /**

 - Alias to the global object must not cause internal errors.
    */
   public void testGlobalObjectAlias() {
 testSame("var global=window;global.alert(1)");
   }

  /**

 - Deep prototype chain with classes A -> B -> C.
    */
   public void testDeepPrototypeChain() {
 testSame(
    "function A(){}" +
    "A.prototype.a=function(){};" +
    "function B(){}" +
    "B.prototype=new A();" +
    "B.prototype.b=function(){};" +
    "function C(){}" +
    "C.prototype=new B();" +
    "C.prototype.c=function(){};" +
    "new C().a();new C().b();new C().c()");
   }

  /**

 - Empty script is a valid boundary case.
    */
   public void testEmptyScript() {
 testSame("");
   }

  /**

 - Externally-referencable names (document) must not be removed.
    */
   public void testExternNotRemoved() {
 testSame("var d=document;d.getElementById('x')");
   }

  /**

 - Assignment to a property via a computed alias chain must not crash.
    */
   public void testAliasChainAssignment() {
 testSame(
    "function Foo(){}" +
    "var a=Foo.prototype;" +
    "var b=a;" +
    "b.prop=function(){};" +
    "new Foo().prop()");
   }

  /**

 - Combined alias and prototype-inherited reference must be resolved.
    */
   public void testInheritedAliasReference() {
 testSame(
    "function Base(){}" +
    "Base.prototype.baseMethod=function(){};" +
    "function Derived(){}" +
    "Derived.prototype=new Base();" +
    "var dProto=Derived.prototype;" +
    "dProto.baseMethod();" +
    "dProto.ownMethod=function(){};" +
    "new Derived().ownMethod()");
   }

  /**

 - Verify that a setter on a property which is an alias still works.
    */
   public void testSetterOnAliasedProperty() {
 testSame(
    "var obj={};" +
    "obj.a={};" +
    "var alias=obj.a;" +
    "alias.b=42;" +
    "alert(obj.a.b)");
   }
 }
