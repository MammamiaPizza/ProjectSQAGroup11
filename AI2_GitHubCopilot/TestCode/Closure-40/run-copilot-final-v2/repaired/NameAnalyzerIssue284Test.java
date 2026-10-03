package com.google.javascript.jscomp;

 import junit.framework.TestCase;
 import java.util.Collections;

 /**
  * Tests for NameAnalyzer focusing on correct handling of prototype chains,
  * aliases and class-defining functions (issue 284).
  */
 public class NameAnalyzerIssue284Test extends TestCase {

   public void testPrototypeChain() {
     testSame(
         "function Foo(){}" +
         "Foo.prototype.bar=function(){};" +
         "new Foo().bar()");
   }

   public void testPrototypeAliasAssignment() {
     testSame(
         "function Foo(){}" +
         "Foo.prototype.bar=function(){};" +
         "var alias=Foo.prototype;" +
         "alias.baz=function(){};" +
         "new Foo().baz()");
   }

   public void testHiddenAliasFromIIFE() {
     testSame(
         "function Foo(){}" +
         "var alias=(function(){return Foo.prototype})();" +
         "alias.m=function(){};" +
         "new Foo().m()");
   }

   public void testClassDefiningInheritance() {
     testSame(
         "function Parent(){}" +
         "function Child(){}" +
         "Child.prototype=new Parent();" +
         "Child.prototype.myMethod=function(){};" +
         "new Child().myMethod()");
   }

   public void testUnusedVariableRemoved() {
     test("var unused=1;function keep(){return keep}",
          "function keep(){return keep}");
   }

   public void testGlobalObjectAlias() {
     testSame("var global=window;global.alert(1)");
   }

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

   public void testEmptyScript() {
     testSame("");
   }

   public void testExternNotRemoved() {
     testSame("var d=document;d.getElementById('x')");
   }

   public void testAliasChainAssignment() {
     testSame(
         "function Foo(){}" +
         "var a=Foo.prototype;" +
         "var b=a;" +
         "b.prop=function(){};" +
         "new Foo().prop()");
   }

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

   public void testSetterOnAliasedProperty() {
     testSame(
         "var obj={};" +
         "obj.a={};" +
         "var alias=obj.a;" +
         "alias.b=42;" +
         "alert(obj.a.b)");
   }

   private void testSame(String js) {
     test(js, null);
   }

   private void test(String js, String expected) {
     Compiler compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     compiler.init(options);
     compiler.compile(
         CommandLineRunner.getDefaultExterns(),
         new JSSourceFile[]{JSSourceFile.fromCode("test.js", js)},
         options);
     assertFalse("Compilation produced errors", compiler.hasErrors());
     try {
       NameAnalyzer analyzer = new NameAnalyzer(compiler, true);
       analyzer.process(compiler.getExternsRoot(), compiler.getRoot());
     } catch (RuntimeException e) {
       fail("Internal compiler error: " + e.getMessage());
     }
     if (expected != null) {
       String result = compiler.toSource();
       assertEquals(normalize(expected), normalize(result));
     }
   }

   private static String normalize(String s) {
     return s.trim().replaceAll("\\s+", " ").replaceAll(";\\s*", ";");
   }
 }
